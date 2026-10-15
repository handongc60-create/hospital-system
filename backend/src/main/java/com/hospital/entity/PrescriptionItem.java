package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方明细实体类
 */
@Data
@TableName("prescription_item")
public class PrescriptionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String itemId;

    /**
     * 处方ID
     */
    private String prescId;

    /**
     * 药品ID
     */
    private String medicineId;

    /**
     * 药品编码
     */
    private String medicineCode;

    /**
     * 药品名称
     */
    private String medicineName;

    /**
     * 药品规格
     */
    private String specification;

    /**
     * 药品类型：西药/中成药/中药饮片
     */
    private String medicineType;

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
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

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

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}
