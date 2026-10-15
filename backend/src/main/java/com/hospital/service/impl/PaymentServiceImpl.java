package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.common.ResultCode;
import com.hospital.dto.PaymentDTO;
import com.hospital.entity.Appointment;
import com.hospital.entity.Payment;
import com.hospital.entity.Prescription;
import com.hospital.mapper.AppointmentMapper;
import com.hospital.mapper.PaymentMapper;
import com.hospital.mapper.PrescriptionMapper;
import com.hospital.service.PaymentService;
import com.hospital.vo.PaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PaymentVO> createPayment(PaymentDTO dto) {
        if (dto.getPatientId() == null || dto.getBusinessType() == null
                || dto.getBusinessId() == null || dto.getPayMethod() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        BigDecimal amount = BigDecimal.ZERO;
        String apptId = null;
        String prescId = null;

        if ("appointment".equals(dto.getBusinessType())) {
            Appointment appt = appointmentMapper.selectById(dto.getBusinessId());
            if (appt == null) {
                return Result.error(ResultCode.APPOINTMENT_NOT_EXIST);
            }
            if (appt.getPayStatus() == 1) {
                return Result.error(ResultCode.PAYMENT_ALREADY_PAID);
            }
            amount = appt.getFee();
            apptId = appt.getApptId();
        } else if ("prescription".equals(dto.getBusinessType())) {
            Prescription presc = prescriptionMapper.selectById(dto.getBusinessId());
            if (presc == null) {
                return Result.error(ResultCode.PRESCRIPTION_NOT_EXIST);
            }
            amount = presc.getTotalAmount();
            prescId = presc.getPrescId();
        } else {
            return Result.error(ResultCode.PARAM_ERROR.getCode(), "不支持的业务类型");
        }

        if (dto.getAmount() == null || dto.getAmount().compareTo(amount) != 0) {
            return Result.error(ResultCode.PAYMENT_AMOUNT_ERROR);
        }

        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        if (apptId != null) {
            wrapper.eq(Payment::getApptId, apptId);
        }
        if (prescId != null) {
            wrapper.eq(Payment::getPrescId, prescId);
        }
        wrapper.eq(Payment::getStatus, "待支付");
        Payment existPayment = paymentMapper.selectOne(wrapper);
        if (existPayment != null) {
            PaymentVO vo = convertToVO(existPayment);
            return Result.success(vo);
        }

        Payment payment = new Payment();
        payment.setPayNo(generatePayNo());
        payment.setApptId(apptId);
        payment.setPrescId(prescId);
        payment.setPatientId(dto.getPatientId());
        payment.setBusinessType(dto.getBusinessType());
        payment.setAmount(amount);
        payment.setPayMethod(dto.getPayMethod());
        payment.setStatus("待支付");
        payment.setOperatorId(dto.getOperatorId());
        paymentMapper.insert(payment);

        PaymentVO vo = convertToVO(payment);
        if ("微信".equals(dto.getPayMethod()) || "支付宝".equals(dto.getPayMethod())) {
            vo.setPayUrl("https://pay.example.com/" + payment.getPayId());
            vo.setQrCode("weixin://wxpay/bizpayurl?pr=" + UUID.randomUUID());
        }

        log.info("创建支付订单: payId={}, amount={}, method={}",
                payment.getPayId(), amount, dto.getPayMethod());
        return Result.success("订单创建成功", vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> payCallback(String payId, String tradeNo, boolean success) {
        Payment payment = paymentMapper.selectById(payId);
        if (payment == null) {
            return Result.error(ResultCode.PAYMENT_NOT_EXIST);
        }

        if ("已支付".equals(payment.getStatus())) {
            return Result.success("重复回调，已处理");
        }

        if (success) {
            payment.setStatus("已支付");
            payment.setPayTime(LocalDateTime.now());
            payment.setTradeNo(tradeNo);
            paymentMapper.updateById(payment);

            if (payment.getApptId() != null) {
                Appointment appt = new Appointment();
                appt.setApptId(payment.getApptId());
                appt.setPayStatus(1);
                appt.setPayMethod(payment.getPayMethod());
                appt.setPayTime(LocalDateTime.now());
                appt.setTradeNo(tradeNo);
                appointmentMapper.updateById(appt);
            }
            if (payment.getPrescId() != null) {
                Prescription presc = new Prescription();
                presc.setPrescId(payment.getPrescId());
                presc.setStatus("已缴费");
                presc.setPayTime(LocalDateTime.now());
                prescriptionMapper.updateById(presc);
            }

            log.info("支付成功: payId={}, amount={}, tradeNo={}",
                    payId, payment.getAmount(), tradeNo);
        } else {
            payment.setStatus("支付失败");
            paymentMapper.updateById(payment);
            log.info("支付失败: payId={}", payId);
        }

        return Result.success("处理完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> refund(String payId, String reason, String operator) {
        Payment payment = paymentMapper.selectById(payId);
        if (payment == null) {
            return Result.error(ResultCode.PAYMENT_NOT_EXIST);
        }
        if (!"已支付".equals(payment.getStatus())) {
            return Result.error(ResultCode.REFUND_NOT_ALLOWED.getCode(), "只有已支付的订单才能退费");
        }

        payment.setStatus("已退费");
        payment.setRefundTime(LocalDateTime.now());
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundReason(reason);
        payment.setRefundTradeNo("REFUND" + System.currentTimeMillis());
        paymentMapper.updateById(payment);

        if (payment.getApptId() != null) {
            Appointment appt = appointmentMapper.selectById(payment.getApptId());
            if (appt != null) {
                appt.setPayStatus(2);
                appointmentMapper.updateById(appt);
            }
        }
        if (payment.getPrescId() != null) {
            Prescription presc = prescriptionMapper.selectById(payment.getPrescId());
            if (presc != null) {
                presc.setStatus("已退费");
                prescriptionMapper.updateById(presc);
            }
        }

        log.info("退费成功: payId={}, amount={}, reason={}", payId, payment.getAmount(), reason);
        return Result.success("退费成功", null);
    }

    @Override
    public Result<PaymentVO> getPaymentDetail(String payId) {
        Payment payment = paymentMapper.selectById(payId);
        if (payment == null) {
            return Result.error(ResultCode.PAYMENT_NOT_EXIST);
        }
        return Result.success(convertToVO(payment));
    }

    @Override
    public Result<Page<PaymentVO>> getPatientPayments(String patientId, String status,
                                                        Integer page, Integer size) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getPatientId, patientId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreateTime);

        Page<Payment> pageParam = new Page<>(page, size);
        Page<Payment> result = paymentMapper.selectPage(pageParam, wrapper);

        Page<PaymentVO> voPage = new Page<>(page, size, result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO).collect(java.util.stream.Collectors.toList()));

        return Result.success(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PaymentVO> onsitePayment(PaymentDTO dto) {
        // 收费员现场缴费：直接找到该挂号的待支付记录，标记为已支付
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getApptId, dto.getBusinessId())
               .eq(Payment::getStatus, "待支付")
               .orderByDesc(Payment::getCreateTime)
               .last("LIMIT 1");
        Payment payment = paymentMapper.selectOne(wrapper);
        if (payment == null) {
            return Result.error("没有待支付的挂号记录");
        }

        // 标记为已支付
        payment.setStatus("已支付");
        payment.setPayTime(LocalDateTime.now());
        payment.setPayMethod(dto.getPayMethod() != null ? dto.getPayMethod() : "现金");
        payment.setTradeNo("ONSITE" + System.currentTimeMillis());
        payment.setOperatorId(dto.getOperatorId());
        paymentMapper.updateById(payment);

        // 更新挂号记录为已支付
        Appointment appt = appointmentMapper.selectById(payment.getApptId());
        if (appt != null) {
            appt.setPayStatus(1);
            appt.setPayMethod(payment.getPayMethod());
            appt.setPayTime(LocalDateTime.now());
            appointmentMapper.updateById(appt);
        }

        log.info("现场收费成功: payId={}, apptId={}, amount={}",
                payment.getPayId(), payment.getApptId(), payment.getAmount());
        return Result.success("收费成功", convertToVO(payment));
    }

    @Override
    public Result<Page<PaymentVO>> getCashierPayments(String apptNo, Integer page, Integer size) {
        Page<PaymentVO> pageParam = new Page<>(page, size);
        Page<PaymentVO> result = paymentMapper.selectCashierPayments(pageParam, apptNo);
        result.getRecords().forEach(vo -> {
            vo.setStatusDesc(getStatusDesc(vo.getStatus()));
            vo.setBusinessTypeDesc(getBusinessTypeDesc(vo.getBusinessType()));
        });
        return Result.success(result);
    }

    private String generatePayNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private PaymentVO convertToVO(Payment payment) {
        PaymentVO vo = new PaymentVO();
        BeanUtils.copyProperties(payment, vo);
        vo.setStatusDesc(getStatusDesc(payment.getStatus()));
        vo.setBusinessTypeDesc(getBusinessTypeDesc(payment.getBusinessType()));
        return vo;
    }

    private String getStatusDesc(String status) {
        if (status == null) return "";
        switch (status) {
            case "待支付": return "待支付";
            case "已支付": return "已支付";
            case "已退费": return "已退费";
            case "支付失败": return "支付失败";
            case "已关闭": return "已关闭";
            default: return status;
        }
    }

    private String getBusinessTypeDesc(String type) {
        if (type == null) return "";
        switch (type) {
            case "appointment": return "挂号费";
            case "prescription": return "处方费";
            default: return type;
        }
    }
}
