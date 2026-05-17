package com.example.server.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommissionService {

    public BigDecimal calculate(BigDecimal price, BigDecimal rate) {
        if (price == null || rate == null) return BigDecimal.ZERO;
        return price.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculate(BigDecimal price, BigDecimal rate, int quantity) {
        return calculate(price, rate).multiply(BigDecimal.valueOf(quantity));
    }

    public String commissionSummary(Long orderId, Long salesId, BigDecimal total) {
        return "订单 " + orderId + " 由分销员 " + salesId + " 推广，佣金待结算：¥" + total;
    }
}
