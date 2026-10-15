package com.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hospital.common.Result;
import com.hospital.dto.AppointmentDTO;
import com.hospital.vo.AppointmentVO;
import com.hospital.vo.ScheduleVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 挂号服务接口
 */
public interface AppointmentService {

    /**
     * 预约挂号
     *
     * @param appointmentDTO 挂号请求
     * @return 挂号结果
     */
    Result<AppointmentVO> makeAppointment(AppointmentDTO appointmentDTO);

    /**
     * 取消预约（退号）
     *
     * @param apptId   挂号ID
     * @param reason   退号原因
     * @param operator 操作人
     * @return 退号结果
     */
    Result<Void> cancelAppointment(String apptId, String reason, String operator);

    /**
     * 查询可预约排班列表
     *
     * @param deptId    科室ID
     * @param doctorId  医生ID（可选）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班列表
     */
    Result<List<ScheduleVO>> getAvailableSchedules(String deptId, String doctorId,
                                                     LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询患者挂号记录
     *
     * @param patientId 患者ID
     * @param status    就诊状态（可选）
     * @param page      页码
     * @param size      每页大小
     * @return 挂号记录分页
     */
    Result<IPage<AppointmentVO>> getPatientAppointments(String patientId, String status,
                                                         Integer page, Integer size);

    /**
     * 查询挂号详情
     *
     * @param apptId 挂号ID
     * @return 挂号详情
     */
    Result<AppointmentVO> getAppointmentDetail(String apptId);

    /**
     * 医生叫号
     *
     * @param doctorId 医生ID
     * @param apptId   挂号ID（可选，不传则叫下一位）
     * @return 当前患者挂号信息
     */
    Result<AppointmentVO> callNextPatient(String doctorId, String apptId);

    /**
     * 完成看诊
     *
     * @param apptId 挂号ID
     * @return 操作结果
     */
    Result<Void> finishVisit(String apptId);

    /**
     * 查询医生当日挂号队列
     *
     * @param doctorId   医生ID
     * @param visitDate  就诊日期
     * @param visitStatus 就诊状态（可选）
     * @param page       页码
     * @param size       每页大小
     * @return 挂号队列
     */
    Result<IPage<AppointmentVO>> getDoctorAppointments(String doctorId, String visitDate,
                                                        String visitStatus, Integer page, Integer size);
}
