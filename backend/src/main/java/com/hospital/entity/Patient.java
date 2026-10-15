package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 患者实体类
 */
@Data
@TableName("patient")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 患者ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String patientId;

    /**
     * 关联用户ID
     */
    private String userId;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：男/女
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 医保卡号
     */
    private String medicalCard;

    /**
     * 医保类型：城镇职工医保/城乡居民医保/自费
     */
    private String insuranceType;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    private String emergencyPhone;

    /**
     * 过敏史
     */
    private String allergyHistory;

    /**
     * 既往病史
     */
    private String pastHistory;

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
