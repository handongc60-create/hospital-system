package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.dto.PaymentDTO;
import com.hospital.service.PaymentService;
import com.hospital.util.JwtUtil;
import com.hospital.vo.PaymentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 支付控制器
 */
@Api(tags = "支付管理")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("创建支付订单")
    @PostMapping("/create")
    public Result<PaymentVO> createPayment(@Valid @RequestBody PaymentDTO dto,
                                            HttpServletRequest request) {
        String patientId = getPatientId(request);
        dto.setPatientId(patientId);
        return paymentService.createPayment(dto);
    }

    @ApiOperation("支付回调（第三方支付平台调用）")
    @PostMapping("/callback")
    public Result<String> payCallback(
            @RequestParam String payId,
            @RequestParam String tradeNo,
            @RequestParam boolean success) {
        return paymentService.payCallback(payId, tradeNo, success);
    }

    @ApiOperation("退费处理")
    @PostMapping("/refund/{payId}")
    public Result<Void> refund(@PathVariable String payId,
                                @RequestParam String reason,
                                HttpServletRequest request) {
        String operator = getUserId(request);
        return paymentService.refund(payId, reason, operator);
    }

    @ApiOperation("查询支付详情")
    @GetMapping("/{payId}")
    public Result<PaymentVO> getPaymentDetail(@PathVariable String payId) {
        return paymentService.getPaymentDetail(payId);
    }

    @ApiOperation("查询我的支付记录")
    @GetMapping("/my")
    public Result<Page<PaymentVO>> getMyPayments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        String patientId = getPatientId(request);
        return paymentService.getPatientPayments(patientId, status, page, size);
    }

    @ApiOperation("现场收费（收费员使用）")
    @PostMapping("/onsite")
    public Result<PaymentVO> onsitePayment(@Valid @RequestBody PaymentDTO dto,
                                            HttpServletRequest request) {
        String operator = getUserId(request);
        dto.setOperatorId(operator);
        return paymentService.onsitePayment(dto);
    }

    @ApiOperation("收费员：分页查询所有支付记录")
    @GetMapping("/cashier/list")
    public Result<Page<PaymentVO>> getCashierPayments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String apptId) {
        return paymentService.getCashierPayments(apptId, page, size);
    }

    private String getUserId(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader(jwtUtil.getHeader()));
        return token != null ? jwtUtil.getUserIdFromToken(token) : null;
    }

    private String getPatientId(HttpServletRequest request) {
        return getUserId(request);
    }
}
