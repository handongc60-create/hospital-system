package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.service.UserService;
import com.hospital.util.JwtUtil;
import com.hospital.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @ApiOperation("患者注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        String userId = token != null ? jwtUtil.getUserIdFromToken(token) : null;
        return userService.logout(userId);
    }

    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        String userId = jwtUtil.getUserIdFromToken(token);
        return userService.changePassword(userId, oldPassword, newPassword);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginVO> getUserInfo(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        if (token == null || !jwtUtil.validateToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        LoginVO vo = new LoginVO();
        vo.setUserId(jwtUtil.getUserIdFromToken(token));
        vo.setUsername(jwtUtil.getUsernameFromToken(token));
        vo.setRole(jwtUtil.getRoleFromToken(token));
        return Result.success(vo);
    }
}
