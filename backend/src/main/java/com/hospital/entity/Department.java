package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科室实体类
 */
@Data
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 科室ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 科室编码
     */
    private String deptCode;

    /**
     * 科室位置
     */
    private String location;

    /**
     * 科室描述
     */
    private String description;

    /**
     * 科室主任ID
     */
    private String leaderId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 科室类型：临床科室/医技科室/行政科室
     */
    private String deptType;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-停用，1-正常
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
