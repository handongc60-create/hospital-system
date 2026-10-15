package com.hospital.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 排班响应VO
 */
@Data
public class ScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    private String scheduleId;

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
     * 出诊日期
     */
    private LocalDate workDate;

    /**
     * 时段：上午/下午/晚间
     */
    private String timeSlot;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 最大号源数
     */
    private Integer maxCount;

    /**
     * 剩余号源数
     */
    private Integer remainCount;

    /**
     * 已挂号数
     */
    private Integer bookedCount;

    /**
     * 挂号费
     */
    private BigDecimal fee;

    /**
     * 排班类型
     */
    private String scheduleType;

    /**
     * 诊室
     */
    private String clinicRoom;

    /**
     * 状态：0-停诊，1-正常
     */
    private Integer status;

    /**
     * 是否可预约
     */
    private Boolean available;
}
