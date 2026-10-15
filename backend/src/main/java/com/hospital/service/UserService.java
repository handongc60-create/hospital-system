package com.hospital.service;

import com.hospital.common.Result;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.vo.LoginVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录结果（含Token）
     */
    Result<LoginVO> login(LoginDTO loginDTO);

    /**
     * 患者注册
     *
     * @param registerDTO 注册请求
     * @return 注册结果
     */
    Result<Void> register(RegisterDTO registerDTO);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     * @return 登出结果
     */
    Result<Void> logout(String userId);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    Result<Void> changePassword(String userId, String oldPassword, String newPassword);
}
