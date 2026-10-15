package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.entity.Department;
import com.hospital.entity.User;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.UserMapper;
import com.hospital.util.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 管理员控制器（用户管理、科室管理）
 */
@Api(tags = "管理员功能")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    // ==================== 用户管理 ====================

    @ApiOperation("分页查询所有用户")
    @GetMapping("/users")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> pageParam = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        // 清除密码字段，不返回给前端
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @ApiOperation("添加用户")
    @PostMapping("/users")
    public Result<Void> addUser(@RequestBody User user) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 生成用户ID
        user.setUserId("user" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        // 密码加密（默认密码123456）
        String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        user.setSalt(salt);
        String rawPassword = (user.getPassword() != null && !user.getPassword().isEmpty()) ? user.getPassword() : "123456";
        user.setPassword(MD5Util.md5WithSalt(rawPassword, salt));
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(1);
        userMapper.insert(user);
        return Result.success("添加成功", null);
    }

    @ApiOperation("修改用户")
    @PutMapping("/users/{userId}")
    public Result<Void> updateUser(@PathVariable String userId, @RequestBody User user) {
        User existUser = userMapper.selectById(userId);
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        user.setUserId(userId);
        // 如果有新密码，则更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String salt = existUser.getSalt();
            user.setPassword(MD5Util.md5WithSalt(user.getPassword(), salt));
        } else {
            user.setPassword(null); // 不修改密码
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success("修改成功", null);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            return Result.error("不能删除管理员账号");
        }
        userMapper.deleteById(userId);
        return Result.success("删除成功", null);
    }

    // ==================== 科室管理 ====================

    @ApiOperation("查询所有科室（含停用）")
    @GetMapping("/departments")
    public Result<Page<Department>> getDepartmentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Department::getDeptName, keyword);
        }
        wrapper.orderByAsc(Department::getSortOrder);

        Page<Department> pageParam = new Page<>(page, size);
        Page<Department> result = departmentMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }

    @ApiOperation("添加科室")
    @PostMapping("/departments")
    public Result<Void> addDepartment(@RequestBody Department dept) {
        // 检查科室名称是否存在
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDeptName, dept.getDeptName());
        if (departmentMapper.selectCount(wrapper) > 0) {
            return Result.error("科室名称已存在");
        }
        dept.setDeptId("dept" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        dept.setCreateTime(LocalDateTime.now());
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }
        departmentMapper.insert(dept);
        return Result.success("添加成功", null);
    }

    @ApiOperation("修改科室")
    @PutMapping("/departments/{deptId}")
    public Result<Void> updateDepartment(@PathVariable String deptId, @RequestBody Department dept) {
        Department existDept = departmentMapper.selectById(deptId);
        if (existDept == null) {
            return Result.error("科室不存在");
        }
        dept.setDeptId(deptId);
        dept.setUpdateTime(LocalDateTime.now());
        departmentMapper.updateById(dept);
        return Result.success("修改成功", null);
    }

    @ApiOperation("删除科室")
    @DeleteMapping("/departments/{deptId}")
    public Result<Void> deleteDepartment(@PathVariable String deptId) {
        Department dept = departmentMapper.selectById(deptId);
        if (dept == null) {
            return Result.error("科室不存在");
        }
        departmentMapper.deleteById(deptId);
        return Result.success("删除成功", null);
    }
}
