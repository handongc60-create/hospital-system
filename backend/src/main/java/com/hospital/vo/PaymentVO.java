package com.hospital.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录响应VO
 */
@Data
public class PaymentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String payId;

    private String payNo;

    private String apptId;

    /** 友好挂号编号，如 GH20260913-002 */
    private String apptNo;

    private String prescId;

    private String patientId;

    private String patientName;

    private String businessType;

    private String businessTypeDesc;

    private BigDecimal amount;

    private String payMethod;

    private String status;

    private String statusDesc;

    private LocalDateTime payTime;

    private String tradeNo;

    private LocalDateTime refundTime;

    private BigDecimal refundAmount;

    private String refundReason;

    private String operatorName;

    private LocalDateTime createTime;

    private String payUrl;

    private String qrCode;
}
