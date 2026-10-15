package com.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.Doctor;
import com.hospital.mapper.DoctorMapper;
import com.hospital.service.AppointmentService;
import com.hospital.util.JwtUtil;
import com.hospital.vo.AppointmentVO;
import com.hospital.vo.ScheduleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 挂号控制器
 */
@Api(tags = "挂号管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DoctorMapper doctorMapper;

    @ApiOperation("预约挂号")
    @PostMapping("/make")
    public Result<AppointmentVO> makeAppointment(@Valid @RequestBody AppointmentDTO dto,
                                                   HttpServletRequest request) {
        String patientId = getPatientId(request);
        dto.setPatientId(patientId);
        return appointmentService.makeAppointment(dto);
    }

    @ApiOperation("取消预约（退号）")
    @PostMapping("/cancel/{apptId}")
    public Result<Void> cancelAppointment(@PathVariable String apptId,
                                           @RequestParam(required = false) String reason,
                                           HttpServletRequest request) {
        String operator = getUserId(request);
        return appointmentService.cancelAppointment(apptId, reason, operator);
    }

    @ApiOperation("查询可预约排班")
    @GetMapping("/schedules")
    public Result<List<ScheduleVO>> getAvailableSchedules(
            @RequestParam String deptId,
            @RequestParam(required = false) String doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return appointmentService.getAvailableSchedules(deptId, doctorId, startDate, endDate);
    }

    @ApiOperation("查询我的挂号记录")
    @GetMapping("/my")
    public Result<IPage<AppointmentVO>> getMyAppointments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        String patientId = getPatientId(request);
        return appointmentService.getPatientAppointments(patientId, status, page, size);
    }

    @ApiOperation("查询挂号详情")
    @GetMapping("/{apptId}")
    public Result<AppointmentVO> getAppointmentDetail(@PathVariable String apptId) {
        return appointmentService.getAppointmentDetail(apptId);
    }

    @ApiOperation("医生叫号")
    @PostMapping("/call")
    public Result<AppointmentVO> callNextPatient(
            @RequestParam(required = false) String apptId,
            HttpServletRequest request) {
        String doctorId = getDoctorId(request);
        return appointmentService.callNextPatient(doctorId, apptId);
    }

    @ApiOperation("完成看诊")
    @PostMapping("/finish/{apptId}")
    public Result<Void> finishVisit(@PathVariable String apptId) {
        return appointmentService.finishVisit(apptId);
    }

    @ApiOperation("查询医生当日挂号队列")
    @GetMapping("/doctor/queue")
    public Result<IPage<AppointmentVO>> getDoctorAppointments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String visitDate,
            @RequestParam(required = false) String visitStatus,
            HttpServletRequest request) {
        String doctorId = getDoctorId(request);
        return appointmentService.getDoctorAppointments(doctorId, visitDate, visitStatus, page, size);
    }

    /**
     * 从Token中获取用户ID
     */
    private String getUserId(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        return token != null ? jwtUtil.getUserIdFromToken(token) : null;
    }

    /**
     * 从Token中获取医生业务ID（doctor_id）。
     * Token里存的是sys_user.user_id，而appointment.doctor_id存的是doctor.doctor_id，
     * 必须通过doctor.user_id关联表把登录账号ID翻译成医生业务ID，否则队列/叫号会查不到数据。
     */
    private String getDoctorId(HttpServletRequest request) {
        String userId = getUserId(request);
        if (userId == null) {
            return null;
        }
        Doctor doctor = doctorMapper.selectByUserId(userId);
        if (doctor == null) {
            throw new com.hospital.exception.BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return doctor.getDoctorId();
    }

    /**
     * 从Token中获取患者ID（简化：实际应通过userId查询patient表）
     */
    private String getPatientId(HttpServletRequest request) {
        // 简化实现，实际应根据userId查询patient表获取patientId
        return getUserId(request);
    }
}
