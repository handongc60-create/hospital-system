package com.hospital.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录响应VO
 */
@Data
public class AppointmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 挂号ID
     */
    private String apptId;

    /**
     * 友好挂号编号：GH+日期+排队号，如 GH20260913-002
     */
    private String apptNo;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 医生ID
     */
    private String doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 医生职称
     */
    private String doctorTitle;

    /**
     * 科室ID
     */
    private String deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 排班ID
     */
    private String scheduleId;

    /**
     * 挂号时间
     */
    private LocalDateTime apptTime;

    /**
     * 就诊日期
     */
    private LocalDate visitDate;

    /**
     * 就诊时段
     */
    private String timeSlot;

    /**
     * 具体就诊时间点，如 "08:30"
     */
    private String visitTime;

    /**
     * 排队号
     */
    private Integer queueNo;

    /**
     * 挂号费
     */
    private BigDecimal fee;

    /**
     * 支付状态：0-未支付，1-已支付，2-已退费
     */
    private Integer payStatus;

    /**
     * 支付状态描述
     */
    private String payStatusDesc;

    /**
     * 就诊状态：待就诊/就诊中/已就诊/已退号
     */
    private String visitStatus;

    /**
     * 挂号来源
     */
    private String source;

    /**
     * 支付方式
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 诊室
     */
    private String clinicRoom;

    /**
     * 是否可以退号
     */
    private Boolean canCancel;
}
