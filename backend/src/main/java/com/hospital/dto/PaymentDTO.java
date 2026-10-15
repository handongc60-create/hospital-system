package com.hospital.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付请求DTO
 */
@Data
public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型：appointment-挂号费，prescription-处方费
     */
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    /**
     * 业务ID（挂号ID或处方ID）
     */
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    /**
     * 患者ID（从Token中获取）
     */
    private String patientId;

    /**
     * 支付方式：微信/支付宝/医保卡/现金/银行卡
     */
    @NotBlank(message = "支付方式不能为空")
    private String payMethod;

    /**
     * 支付金额（前端传，后端校验）
     */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    /**
     * 操作人ID（现场收费时传）
     */
    private String operatorId;
}
