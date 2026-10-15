package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.entity.Payment;
import com.hospital.vo.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 根据支付单号查询
     */
    @Select("SELECT * FROM payment WHERE pay_no = #{payNo} AND deleted = 0")
    Payment selectByPayNo(@Param("payNo") String payNo);

    /**
     * 根据第三方交易号查询
     */
    @Select("SELECT * FROM payment WHERE trade_no = #{tradeNo} AND deleted = 0")
    Payment selectByTradeNo(@Param("tradeNo") String tradeNo);

    /**
     * 收费员：分页查询支付记录（关联挂号编号、患者姓名）
     */
    Page<PaymentVO> selectCashierPayments(Page<PaymentVO> page,
                                          @Param("apptNo") String apptNo);
}
