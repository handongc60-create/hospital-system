package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生实体类
 */
@Data
@TableName("doctor")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String doctorId;

    /**
     * 关联用户ID
     */
    private String userId;

    /**
     * 所属科室ID
     */
    private String deptId;

    /**
     * 医生姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 职称：主任医师/副主任医师/主治医师/住院医师
     */
    private String title;

    /**
     * 专长
     */
    private String specialty;

    /**
     * 挂号费
     */
    private BigDecimal fee;

    /**
     * 医生简介
     */
    private String intro;

    /**
     * 从业年限
     */
    private Integer experienceYears;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String school;

    /**
     * 状态：0-停诊，1-正常
     */
    private Integer status;

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
