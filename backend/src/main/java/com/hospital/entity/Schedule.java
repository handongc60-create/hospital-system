package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排班实体类
 */
@Data
@TableName("schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String scheduleId;

    /**
     * 医生ID
     */
    private String doctorId;

    /**
     * 科室ID
     */
    private String deptId;

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
     * 排班类型：普通门诊/专家门诊/特需门诊
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
     * 停诊原因
     */
    private String cancelReason;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}
