package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 医生Mapper接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    /**
     * 根据用户ID查询医生
     */
    @Select("SELECT * FROM doctor WHERE user_id = #{userId} AND deleted = 0")
    Doctor selectByUserId(@Param("userId") String userId);

    /**
     * 根据科室ID查询医生列表
     */
    @Select("SELECT * FROM doctor WHERE dept_id = #{deptId} AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Doctor> selectByDeptId(@Param("deptId") String deptId);
}
