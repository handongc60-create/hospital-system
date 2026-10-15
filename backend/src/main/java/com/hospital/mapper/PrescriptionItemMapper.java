package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 处方明细Mapper接口
 */
@Mapper
public interface PrescriptionItemMapper extends BaseMapper<PrescriptionItem> {

    /**
     * 根据处方ID查询明细列表
     */
    @Select("SELECT * FROM prescription_item WHERE presc_id = #{prescId} AND deleted = 0 ORDER BY create_time")
    List<PrescriptionItem> selectByPrescId(@Param("prescId") String prescId);
}
