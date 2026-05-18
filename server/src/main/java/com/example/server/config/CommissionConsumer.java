package com.example.server.config;

import com.example.server.service.OrderService;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommissionConsumer {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderService orderService;

    private static final String QUEUE_KEY = "queue:commission";

    @PostConstruct
    public void start() {
        RBlockingQueue<String> queue = redissonClient.getBlockingQueue(QUEUE_KEY);

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    String orderIdStr = queue.take();
                    try {
                        orderService.processCommission(Long.valueOf(orderIdStr));
                        System.out.println("【异步佣金】订单 " + orderIdStr + " 佣金已结算");
                    } catch (Exception e) {
                        System.err.println("【异步佣金】订单 " + orderIdStr + " 处理失败: " + e.getMessage());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "commission-consumer");
        consumer.setDaemon(true);
        consumer.start();
    }
}
