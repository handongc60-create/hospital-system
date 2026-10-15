package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    /**
     * 根据用户ID查询患者
     */
    @Select("SELECT * FROM patient WHERE user_id = #{userId} AND deleted = 0")
    Patient selectByUserId(@Param("userId") String userId);

    /**
     * 根据身份证号查询患者
     */
    @Select("SELECT * FROM patient WHERE id_card = #{idCard} AND deleted = 0")
    Patient selectByIdCard(@Param("idCard") String idCard);
}
