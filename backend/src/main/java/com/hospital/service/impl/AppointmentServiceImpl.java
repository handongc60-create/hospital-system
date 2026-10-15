package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.*;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.*;
import com.hospital.service.AppointmentService;
import com.hospital.vo.AppointmentVO;
import com.hospital.vo.ScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 挂号服务实现类
 */
@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DepartmentMapper departmentMapper;


    @Autowired
    private com.hospital.mapper.PaymentMapper paymentMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${system.cancel-hours:2}")
    private Integer cancelHours;

    @Value("${system.cache-prefix:hospital:schedule:}")
    private String cachePrefix;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AppointmentVO> makeAppointment(AppointmentDTO dto) {
        // 1. 参数校验
        if (dto.getPatientId() == null || dto.getScheduleId() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 2. 查询排班信息
        Schedule schedule = scheduleMapper.selectById(dto.getScheduleId());
        if (schedule == null || schedule.getStatus() == 0) {
            return Result.error(ResultCode.SCHEDULE_NOT_EXIST);
        }
        if (schedule.getRemainCount() <= 0) {
            return Result.error(ResultCode.SCHEDULE_FULL);
        }

        // 3. 检查是否重复挂号（同一患者同一排班不能重复挂号）
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getPatientId, dto.getPatientId())
               .eq(Appointment::getScheduleId, dto.getScheduleId())
               .ne(Appointment::getVisitStatus, "已退号");
        Long count = appointmentMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            return Result.error(ResultCode.APPOINTMENT_ALREADY_EXIST);
        }

        // 4. 扣减号源（乐观锁保证并发安全）
        int updateCount = scheduleMapper.decreaseRemainCount(
                schedule.getScheduleId(), schedule.getVersion());
        if (updateCount == 0) {
            Schedule latest = scheduleMapper.selectById(dto.getScheduleId());
            if (latest != null && latest.getRemainCount() > 0) {
                updateCount = scheduleMapper.decreaseRemainCount(
                        latest.getScheduleId(), latest.getVersion());
            }
            if (updateCount == 0) {
                return Result.error(ResultCode.SCHEDULE_FULL.getCode(),
                        "挂号失败，号源已被抢完，请选择其他时段");
            }
        }

        // 5. 生成排队号
        Integer maxQueueNo = appointmentMapper.selectMaxQueueNo(dto.getScheduleId());
        int queueNo = (maxQueueNo == null) ? 1 : maxQueueNo + 1;

        // 6. 创建挂号记录
        // 费用以医生标准挂号费为准，保证医生列表/排班页/确认页/实际扣费一致
        Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
        BigDecimal realFee = (doctor != null && doctor.getFee() != null)
                ? doctor.getFee() : schedule.getFee();

        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatientId());
        appointment.setDoctorId(schedule.getDoctorId());
        appointment.setDeptId(schedule.getDeptId());
        appointment.setScheduleId(schedule.getScheduleId());
        appointment.setApptTime(LocalDateTime.now());
        appointment.setVisitDate(schedule.getWorkDate());
        appointment.setTimeSlot(schedule.getTimeSlot());
        appointment.setVisitTime(dto.getVisitTime());
        appointment.setQueueNo(queueNo);
        appointment.setFee(realFee);
        appointment.setPayStatus(0);
        appointment.setVisitStatus("待就诊");
        appointment.setSource(dto.getSource() != null ? dto.getSource() : "线上");
        appointment.setOperatorId(dto.getOperatorId());
        appointmentMapper.insert(appointment);

        // 6.5 自动生成待支付记录，供收费员看到
        try {
            com.hospital.entity.Payment pay = new com.hospital.entity.Payment();
            pay.setPayNo("PAY" + System.currentTimeMillis());
            pay.setApptId(appointment.getApptId());
            pay.setPatientId(dto.getPatientId());
            pay.setBusinessType("appointment");
            pay.setAmount(realFee);
            pay.setPayMethod("现金");
            pay.setStatus("待支付");
            paymentMapper.insert(pay);
        } catch (Exception e) {
            log.warn("自动生成支付记录失败: {}", e.getMessage());
        }

        // 7. 更新Redis缓存中的号源
        if (redisTemplate != null) {
            try {
                String redisKey = cachePrefix + schedule.getScheduleId();
                redisTemplate.opsForValue().decrement(redisKey);
                redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
            } catch (Exception e) {
                log.warn("更新Redis号源缓存失败: {}", e.getMessage());
            }
        }

        // 8. 封装返回结果
        AppointmentVO vo = appointmentMapper.selectDetailById(appointment.getApptId());
        if (vo == null) {
            vo = new AppointmentVO();
            BeanUtils.copyProperties(appointment, vo);
        }

        log.info("预约挂号成功: apptId={}, patientId={}, queueNo={}",
                appointment.getApptId(), dto.getPatientId(), queueNo);
        return Result.success("挂号成功", vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelAppointment(String apptId, String reason, String operator) {
        Appointment appointment = appointmentMapper.selectById(apptId);
        if (appointment == null) {
            return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
        }

        if ("已就诊".equals(appointment.getVisitStatus())) {
            return Result.error(ResultCode.APPOINTMENT_ALREADY_VISITED);
        }
        if ("已退号".equals(appointment.getVisitStatus())) {
            return Result.error("该挂号已退号");
        }

        LocalDateTime visitDateTime = appointment.getVisitDate().atStartOfDay();
        if ("下午".equals(appointment.getTimeSlot())) {
            visitDateTime = visitDateTime.plusHours(14);
        } else if ("晚间".equals(appointment.getTimeSlot())) {
            visitDateTime = visitDateTime.plusHours(18);
        } else {
            visitDateTime = visitDateTime.plusHours(8);
        }
        long hoursUntilVisit = ChronoUnit.HOURS.between(LocalDateTime.now(), visitDateTime);
        if (hoursUntilVisit < cancelHours) {
            return Result.error(ResultCode.APPOINTMENT_CANCEL_TIME_LIMIT);
        }

        appointment.setVisitStatus("已退号");
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setCancelReason(reason);
        appointmentMapper.updateById(appointment);

        scheduleMapper.increaseRemainCount(appointment.getScheduleId());

        if (redisTemplate != null) {
            try {
                String redisKey = cachePrefix + appointment.getScheduleId();
                redisTemplate.opsForValue().increment(redisKey);
            } catch (Exception e) {
                log.warn("更新Redis号源缓存失败: {}", e.getMessage());
            }
        }

        // 退号时同步将待支付/已支付的记录标记为已退款
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.hospital.entity.Payment> pw =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            pw.eq(com.hospital.entity.Payment::getApptId, apptId)
              .in(com.hospital.entity.Payment::getStatus, "待支付", "已支付")
              .last("LIMIT 1");
            com.hospital.entity.Payment pay = paymentMapper.selectOne(pw);
            if (pay != null) {
                pay.setStatus("已退款");
                pay.setUpdateTime(LocalDateTime.now());
                paymentMapper.updateById(pay);
                log.info("退款成功: payId={}, amount={}", pay.getPayId(), pay.getAmount());
            }
        } catch (Exception e) {
            log.warn("退款处理失败: {}", e.getMessage());
        }

        log.info("退号成功: apptId={}, reason={}", apptId, reason);
        return Result.success("退号成功", null);
    }

    @Override
    public Result<List<ScheduleVO>> getAvailableSchedules(String deptId, String doctorId,
                                                            LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = scheduleMapper.selectByCondition(deptId, doctorId, startDate, endDate);
        List<ScheduleVO> voList = new ArrayList<>();

        for (Schedule schedule : schedules) {
            ScheduleVO vo = new ScheduleVO();
            BeanUtils.copyProperties(schedule, vo);

            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                vo.setDoctorName(doctor.getName());
                vo.setDoctorTitle(doctor.getTitle());
                // 费用统一以医生标准挂号费为准
                vo.setFee(doctor.getFee());
            }
            Department dept = departmentMapper.selectById(schedule.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }

            vo.setAvailable(schedule.getRemainCount() > 0);
            voList.add(vo);
        }

        return Result.success(voList);
    }

    @Override
    public Result<IPage<AppointmentVO>> getPatientAppointments(String patientId, String status,
                                                                Integer page, Integer size) {
        Page<AppointmentVO> pageParam = new Page<>(page, size);
        IPage<AppointmentVO> result = appointmentMapper.selectPatientAppointments(pageParam, patientId, status);

        for (AppointmentVO vo : result.getRecords()) {
            vo.setPayStatusDesc(getPayStatusDesc(vo.getPayStatus()));
            vo.setCanCancel(canCancel(vo));
        }

        return Result.success(result);
    }

    @Override
    public Result<AppointmentVO> getAppointmentDetail(String apptId) {
        AppointmentVO vo = appointmentMapper.selectDetailById(apptId);
        if (vo == null) {
            return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
        }
        vo.setPayStatusDesc(getPayStatusDesc(vo.getPayStatus()));
        vo.setCanCancel(canCancel(vo));
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<AppointmentVO> callNextPatient(String doctorId, String apptId) {
        Appointment appointment;
        if (apptId != null) {
            appointment = appointmentMapper.selectById(apptId);
            if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
                return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
            }
        } else {
            LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Appointment::getDoctorId, doctorId)
                   .eq(Appointment::getVisitDate, LocalDate.now())
                   .eq(Appointment::getVisitStatus, "待就诊")
                   .eq(Appointment::getPayStatus, 1)
                   .orderByAsc(Appointment::getQueueNo)
                   .last("LIMIT 1");
            appointment = appointmentMapper.selectOne(wrapper);
            if (appointment == null) {
                return Result.error("暂无待就诊患者");
            }
        }

        appointment.setVisitStatus("就诊中");
        appointment.setCallTime(LocalDateTime.now());
        appointment.setVisitStartTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        AppointmentVO vo = appointmentMapper.selectDetailById(appointment.getApptId());
        log.info("医生叫号: doctorId={}, apptId={}, queueNo={}",
                doctorId, appointment.getApptId(), appointment.getQueueNo());
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> finishVisit(String apptId) {
        Appointment appointment = appointmentMapper.selectById(apptId);
        if (appointment == null) {
            return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
        }

        appointment.setVisitStatus("已就诊");
        appointment.setVisitEndTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        log.info("完成看诊: apptId={}", apptId);
        return Result.success("看诊完成", null);
    }

    @Override
    public Result<IPage<AppointmentVO>> getDoctorAppointments(String doctorId, String visitDate,
                                                               String visitStatus, Integer page, Integer size) {
        Page<AppointmentVO> pageParam = new Page<>(page, size);
        if (visitDate == null) {
            visitDate = LocalDate.now().toString();
        }
        IPage<AppointmentVO> result = appointmentMapper.selectDoctorAppointments(
                pageParam, doctorId, visitDate, visitStatus);

        for (AppointmentVO vo : result.getRecords()) {
            vo.setPayStatusDesc(getPayStatusDesc(vo.getPayStatus()));
        }

        return Result.success(result);
    }

    private String getPayStatusDesc(Integer payStatus) {
        if (payStatus == null) return "";
        switch (payStatus) {
            case 0: return "未支付";
            case 1: return "已支付";
            case 2: return "已退费";
            default: return "未知";
        }
    }

    private boolean canCancel(AppointmentVO vo) {
        if (!"待就诊".equals(vo.getVisitStatus())) {
            return false;
        }
        if (vo.getVisitDate() == null) {
            return false;
        }
        LocalDateTime visitDateTime = vo.getVisitDate().atStartOfDay();
        if ("下午".equals(vo.getTimeSlot())) {
            visitDateTime = visitDateTime.plusHours(14);
        } else if ("晚间".equals(vo.getTimeSlot())) {
            visitDateTime = visitDateTime.plusHours(18);
        } else {
            visitDateTime = visitDateTime.plusHours(8);
        }
        long hoursUntilVisit = ChronoUnit.HOURS.between(LocalDateTime.now(), visitDateTime);
        return hoursUntilVisit >= cancelHours;
    }
}
