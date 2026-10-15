package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 处方Mapper接口
 */
@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {
}
