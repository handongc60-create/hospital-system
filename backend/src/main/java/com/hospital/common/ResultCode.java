package com.hospital.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    // 通用状态码
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关 1000-1999
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "账户已被禁用"),
    OLD_PASSWORD_ERROR(1005, "原密码错误"),
    PHONE_ALREADY_REGISTERED(1006, "手机号已注册"),

    // 挂号相关 2000-2999
    SCHEDULE_NOT_EXIST(2001, "排班不存在或已停诊"),
    SCHEDULE_FULL(2002, "号源已满，请选择其他时段"),
    APPOINTMENT_ALREADY_EXIST(2003, "您已预约该时段，请勿重复挂号"),
    APPOINTMENT_NOT_EXIST(2004, "挂号记录不存在"),
    APPOINTMENT_CANCEL_TIME_LIMIT(2005, "距就诊不足2小时，无法退号"),
    APPOINTMENT_ALREADY_VISITED(2006, "已就诊的挂号无法退号"),
    QUEUE_NO_GENERATE_ERROR(2007, "排队号生成失败"),

    // 缴费相关 3000-3999
    PAYMENT_NOT_EXIST(3001, "支付记录不存在"),
    PAYMENT_ALREADY_PAID(3002, "该订单已支付"),
    PAYMENT_METHOD_NOT_SUPPORT(3003, "不支持的支付方式"),
    PAYMENT_AMOUNT_ERROR(3004, "支付金额错误"),
    PAYMENT_FAILED(3005, "支付失败"),
    REFUND_NOT_ALLOWED(3006, "不符合退费条件"),
    REFUND_FAILED(3007, "退费失败"),

    // 处方相关 4000-4999
    PRESCRIPTION_NOT_EXIST(4001, "处方不存在"),
    PRESCRIPTION_ALREADY_PAID(4002, "该处方已缴费"),
    PRESCRIPTION_ALREADY_DISPENSED(4003, "该处方已取药，无法退费"),

    // 科室/医生相关 5000-5999
    DEPARTMENT_NOT_EXIST(5001, "科室不存在"),
    DEPARTMENT_ALREADY_EXIST(5002, "科室名称已存在"),
    DOCTOR_NOT_EXIST(5003, "医生不存在"),

    // 系统相关 9000-9999
    SYSTEM_BUSY(9001, "系统繁忙，请稍后重试"),
    DATA_ACCESS_ERROR(9002, "数据访问异常"),
    NETWORK_ERROR(9003, "网络异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
