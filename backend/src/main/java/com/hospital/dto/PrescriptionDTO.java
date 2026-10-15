package com.hospital.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 处方开具请求DTO
 */
@Data
public class PrescriptionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联挂号ID
     */
    @NotBlank(message = "挂号ID不能为空")
    private String apptId;

    /**
     * 患者ID
     */
    @NotBlank(message = "患者ID不能为空")
    private String patientId;

    /**
     * 医生ID（从Token中获取）
     */
    private String doctorId;

    /**
     * 处方类型：西药处方/中药处方/检查处方
     */
    @NotBlank(message = "处方类型不能为空")
    private String prescType;

    /**
     * 诊断结果
     */
    @NotBlank(message = "诊断结果不能为空")
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
     * 医嘱备注
     */
    private String remark;

    /**
     * 处方明细列表
     */
    @NotEmpty(message = "处方明细不能为空")
    @Valid
    private List<PrescriptionItemDTO> items;

    /**
     * 处方明细DTO
     */
    @Data
    public static class PrescriptionItemDTO implements Serializable {
        /**
         * 药品ID
         */
        @NotBlank(message = "药品ID不能为空")
        private String medicineId;

        /**
         * 药品名称
         */
        @NotBlank(message = "药品名称不能为空")
        private String medicineName;

        /**
         * 药品规格
         */
        private String specification;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 单位
         */
        private String unit;

        /**
         * 单价
         */
        private java.math.BigDecimal unitPrice;

        /**
         * 用法
         */
        private String usageMethod;

        /**
         * 用量
         */
        private String dosage;

        /**
         * 频次
         */
        private String frequency;

        /**
         * 天数
         */
        private Integer days;
    }
}
