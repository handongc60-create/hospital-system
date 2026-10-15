package com.hospital.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 挂号请求DTO
 */
@Data
public class AppointmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    @NotBlank(message = "排班ID不能为空")
    private String scheduleId;

    /**
     * 患者ID（从Token中获取，前端可不传）
     */
    private String patientId;

    /**
     * 挂号来源：线上/现场/自助机
     */
    private String source = "线上";

    /**
     * 操作人ID（现场挂号时传）
     */
    private String operatorId;

    /**
     * 具体就诊时间点，如 "08:30"
     */
    private String visitTime;
}
