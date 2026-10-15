package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String payId;

    /**
     * 支付单号
     */
    private String payNo;

    /**
     * 关联挂号ID（挂号费时关联）
     */
    private String apptId;

    /**
     * 关联处方ID（处方缴费时关联）
     */
    private String prescId;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 业务类型：appointment-挂号费，prescription-处方费
     */
    private String businessType;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式：微信/支付宝/医保卡/现金/银行卡
     */
    private String payMethod;

    /**
     * 支付状态：待支付/已支付/已退费/支付失败/已关闭
     */
    private String status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 第三方支付状态
     */
    private String tradeStatus;

    /**
     * 退费时间
     */
    private LocalDateTime refundTime;

    /**
     * 退费金额
     */
    private BigDecimal refundAmount;

    /**
     * 退费原因
     */
    private String refundReason;

    /**
     * 退费交易号
     */
    private String refundTradeNo;

    /**
     * 操作人ID（现场收费时记录）
     */
    private String operatorId;

    /**
     * 支付备注
     */
    private String remark;

    /**
     * 回调信息
     */
    private String callbackData;

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
