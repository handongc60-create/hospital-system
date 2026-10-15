package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.Result;
import com.hospital.dto.PaymentDTO;
import com.hospital.vo.PaymentVO;

/**
 * 支付服务接口
 */
public interface PaymentService {

    Result<PaymentVO> createPayment(PaymentDTO paymentDTO);

    Result<String> payCallback(String payId, String tradeNo, boolean success);

    Result<Void> refund(String payId, String reason, String operator);

    Result<PaymentVO> getPaymentDetail(String payId);

    Result<Page<PaymentVO>> getPatientPayments(String patientId, String status,
                                                Integer page, Integer size);

    Result<PaymentVO> onsitePayment(PaymentDTO paymentDTO);

    /**
     * 收费员：分页查询所有支付记录
     */
    Result<Page<PaymentVO>> getCashierPayments(String apptNo, Integer page, Integer size);
}
