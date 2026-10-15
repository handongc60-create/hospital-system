package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.PatientMapper;
import com.hospital.mapper.UserMapper;
import com.hospital.service.UserService;
import com.hospital.util.JwtUtil;
import com.hospital.util.MD5Util;
import com.hospital.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            return Result.error(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 2. 校验状态
        if (user.getStatus() == 0) {
            return Result.error(ResultCode.USER_DISABLED);
        }

        // 3. 校验密码
        String encodedPassword = MD5Util.md5WithSalt(loginDTO.getPassword(), user.getSalt());
        if (!encodedPassword.equals(user.getPassword())) {
            return Result.error(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());

        // 5. 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 6. 封装返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setTokenPrefix(jwtUtil.getPrefix());
        loginVO.setUserId(user.getUserId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setRole(user.getRole());
        loginVO.setAvatar(user.getAvatar());

        // 查询关联业务ID
        if ("patient".equals(user.getRole())) {
            Patient patient = patientMapper.selectByUserId(user.getUserId());
            if (patient != null) {
                loginVO.setBizId(patient.getPatientId());
            }
        }

        log.info("用户登录成功: {}", user.getUsername());
        return Result.success("登录成功", loginVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> register(RegisterDTO registerDTO) {
        // 1. 校验密码一致性
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }

        // 2. 校验用户名是否已存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            return Result.error(ResultCode.USER_ALREADY_EXIST);
        }

        // 3. 校验身份证号是否已注册
        Patient existPatient = patientMapper.selectByIdCard(registerDTO.getIdCard());
        if (existPatient != null) {
            return Result.error("该身份证号已注册");
        }

        // 4. 创建用户
        String salt = MD5Util.generateSalt();
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(MD5Util.md5WithSalt(registerDTO.getPassword(), salt));
        user.setSalt(salt);
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setRole("patient");
        user.setStatus(1);
        userMapper.insert(user);

        // 5. 创建患者信息
        Patient patient = new Patient();
        patient.setUserId(user.getUserId());
        patient.setIdCard(registerDTO.getIdCard());
        patient.setName(registerDTO.getRealName());
        patient.setGender(registerDTO.getGender());
        patient.setPhone(registerDTO.getPhone());
        patient.setMedicalCard(registerDTO.getMedicalCard());
        // 根据身份证号计算年龄
        patient.setAge(calculateAgeFromIdCard(registerDTO.getIdCard()));
        patientMapper.insert(patient);

        log.info("患者注册成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return Result.success("注册成功", null);
    }

    @Override
    public Result<Void> logout(String userId) {
        log.info("用户登出: userId={}", userId);
        return Result.success("登出成功", null);
    }

    @Override
    public Result<Void> changePassword(String userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 校验旧密码
        String encodedOldPassword = MD5Util.md5WithSalt(oldPassword, user.getSalt());
        if (!encodedOldPassword.equals(user.getPassword())) {
            return Result.error(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        String newSalt = MD5Util.generateSalt();
        user.setPassword(MD5Util.md5WithSalt(newPassword, newSalt));
        user.setSalt(newSalt);
        userMapper.updateById(user);

        log.info("用户修改密码成功: userId={}", userId);
        return Result.success("密码修改成功", null);
    }

    /**
     * 根据身份证号计算年龄
     */
    private Integer calculateAgeFromIdCard(String idCard) {
        try {
            String birthYear = idCard.substring(6, 10);
            int year = Integer.parseInt(birthYear);
            int currentYear = LocalDateTime.now().getYear();
            return currentYear - year;
        } catch (Exception e) {
            return null;
        }
    }
}
