package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方实体类
 */
@Data
@TableName("prescription")
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 处方ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String prescId;

    /**
     * 关联挂号ID
     */
    private String apptId;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 医生ID
     */
    private String doctorId;

    /**
     * 科室ID
     */
    private String deptId;

    /**
     * 处方编号
     */
    private String prescNo;

    /**
     * 处方类型：西药处方/中药处方/检查处方
     */
    private String prescType;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 主诉
     */
    private String chiefComplaint;

    /**
     * 现病史
     */
    private String presentIllness;

    /**
     * 药品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 状态：待缴费/已缴费/已退费/已取药/已作废
     */
    private String status;

    /**
     * 医嘱备注
     */
    private String remark;

    /**
     * 开具时间
     */
    private LocalDateTime createTime;

    /**
     * 缴费时间
     */
    private LocalDateTime payTime;

    /**
     * 取药时间
     */
    private LocalDateTime dispenseTime;

    /**
     * 作废时间
     */
    private LocalDateTime cancelTime;

    /**
     * 作废原因
     */
    private String cancelReason;

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
