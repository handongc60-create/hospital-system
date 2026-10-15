package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录实体类
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String apptId;

    private String patientId;

    private String doctorId;

    private String deptId;

    private String scheduleId;

    private LocalDateTime apptTime;

    private LocalDate visitDate;

    /** 就诊时段：上午/下午/晚间 */
    private String timeSlot;

    /** 具体就诊时间点，如 "08:30" */
    private String visitTime;

    private Integer queueNo;

    private BigDecimal fee;

    private Integer payStatus;

    private String visitStatus;

    private String source;

    private String payMethod;

    private LocalDateTime payTime;

    private String tradeNo;

    private LocalDateTime callTime;

    private LocalDateTime visitStartTime;

    private LocalDateTime visitEndTime;

    private LocalDateTime cancelTime;

    private String cancelReason;

    private String operatorId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
