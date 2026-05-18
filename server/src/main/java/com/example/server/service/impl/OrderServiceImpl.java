package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.CommissionLog;
import com.example.server.entity.Order;
import com.example.server.entity.User;
import com.example.server.mapper.CommissionLogMapper;
import com.example.server.mapper.OrderMapper;
import com.example.server.service.OrderService;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10");

    @Autowired
    private UserService userService;

    @Autowired
    private CommissionLogMapper commissionLogMapper;

    @Override
    @Transactional
    public void processCommission(Long orderId) {
        Order order = getById(orderId);
        if (order == null || order.getUserId() == null || order.getTotalAmount() == null) return;

        // ── 防重闸：检查 commission_log 是否已有该订单记录 ──
        Long existingCount = commissionLogMapper.selectCount(
                new LambdaQueryWrapper<CommissionLog>().eq(CommissionLog::getOrderId, orderId));
        if (existingCount != null && existingCount > 0) {
            System.out.println("【佣金防重】订单 " + orderId + " 佣金已发放，跳过");
            return;
        }

        User buyer = userService.getById(order.getUserId().intValue());
        if (buyer == null || buyer.getInviterId() == null) return;

        User inviter = userService.getById(buyer.getInviterId());
        if (inviter == null) return;

        BigDecimal commission = order.getTotalAmount().multiply(COMMISSION_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        // ── 增余额 ──
        inviter.setCommissionBalance(
                inviter.getCommissionBalance() != null
                        ? inviter.getCommissionBalance().add(commission)
                        : commission
        );
        userService.updateById(inviter);

        // ── 写流水（每一分钱有据可查） ──
        CommissionLog log = new CommissionLog();
        log.setOrderId(orderId);
        log.setDistributorId(inviter.getUserId());
        log.setOrderAmount(order.getTotalAmount());
        log.setCommissionRate(COMMISSION_RATE);
        log.setCommissionAmount(commission);
        log.setStatus(1);
        log.setSettleTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        commissionLogMapper.insert(log);

        System.out.println("【佣金】用户 " + buyer.getName() + " 支付订单 ¥" + order.getTotalAmount()
                + "，上级 " + inviter.getName() + " 获得佣金 ¥" + commission + "（流水ID " + log.getId() + "）");
    }
}
