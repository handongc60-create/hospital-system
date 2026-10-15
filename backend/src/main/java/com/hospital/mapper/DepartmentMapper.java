package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 科室Mapper接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 查询所有正常状态的科室
     */
    @Select("SELECT * FROM department WHERE status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Department> selectAllActive();

    /**
     * 根据科室名称查询
     */
    @Select("SELECT * FROM department WHERE dept_name = #{deptName} AND deleted = 0")
    Department selectByName(@Param("deptName") String deptName);
}
