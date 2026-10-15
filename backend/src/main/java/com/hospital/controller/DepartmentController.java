package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.entity.Department;
import com.hospital.entity.Doctor;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.DoctorMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室控制器
 */
@Api(tags = "科室管理")
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @ApiOperation("查询所有科室")
    @GetMapping("/list")
    public Result<List<Department>> list() {
        return Result.success(departmentMapper.selectAllActive());
    }

    @ApiOperation("查询科室详情")
    @GetMapping("/{deptId}")
    public Result<Department> getById(@PathVariable String deptId) {
        return Result.success(departmentMapper.selectById(deptId));
    }

    @ApiOperation("查询科室下的医生列表")
    @GetMapping("/{deptId}/doctors")
    public Result<List<Doctor>> getDoctorsByDept(@PathVariable String deptId) {
        return Result.success(doctorMapper.selectByDeptId(deptId));
    }
}
