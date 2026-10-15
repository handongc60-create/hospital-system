package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Appointment;
import com.hospital.vo.AppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 挂号记录Mapper接口
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    /**
     * 查询某排班的最大排队号
     */
    @Select("SELECT MAX(queue_no) FROM appointment " +
            "WHERE schedule_id = #{scheduleId} AND visit_status != '已退号' AND deleted = 0")
    Integer selectMaxQueueNo(@Param("scheduleId") String scheduleId);

    /**
     * 分页查询患者挂号记录
     */
    IPage<AppointmentVO> selectPatientAppointments(Page<AppointmentVO> page,
                                                     @Param("patientId") String patientId,
                                                     @Param("status") String status);

    /**
     * 分页查询医生当日挂号队列
     */
    IPage<AppointmentVO> selectDoctorAppointments(Page<AppointmentVO> page,
                                                    @Param("doctorId") String doctorId,
                                                    @Param("visitDate") String visitDate,
                                                    @Param("visitStatus") String visitStatus);

    /**
     * 根据挂号ID查询详情（含关联信息）
     */
    AppointmentVO selectDetailById(@Param("apptId") String apptId);
}
