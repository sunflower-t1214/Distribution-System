package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.entity.Cart;
import com.example.server.entity.Order;
import com.example.server.entity.OrderItem;
import com.example.server.entity.User;
import com.example.server.service.CartService;
import com.example.server.service.OrderItemService;
import com.example.server.service.OrderService;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final UserService userService;

    // 1. 修复警告：使用构造器注入代替字段注入 (推荐的做法)
    @Autowired
    public OrderController(OrderService orderService, CartService cartService,
                           OrderItemService orderItemService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional
    public Map<String, Object> create(@RequestBody Order order) {
        order.setOrderSn(UUID.randomUUID().toString().replace("-", ""));
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());

        // 分销关联绑定
        User user = userService.getById(order.getUserId());
        if (user != null && user.getSalesId() != null) {
            order.setSalesId(user.getSalesId().longValue());
        }

        if (order.getPayAmount() == null) {
            order.setPayAmount(order.getTotalAmount());
        }

        if (order.getReceiverName() == null) order.setReceiverName("默认收货人");
        if (order.getReceiverPhone() == null) order.setReceiverPhone("13800000000");
        if (order.getReceiverAddress() == null) order.setReceiverAddress("默认地址");

        orderService.save(order);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                item.setOrderId(order.getId());
                orderItemService.save(item);

                cartService.remove(new QueryWrapper<Cart>()
                        .eq("user_id", order.getUserId())
                        .eq("product_id", item.getProductId()));
            }
        }
        // 2. 统一返回格式
        return Map.of("code", 200, "msg", "下单成功");
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Long userId) {
        List<Order> orders = orderService.query()
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .list();

        for (Order order : orders) {
            List<OrderItem> items = orderItemService.query()
                    .eq("order_id", order.getId())
                    .list();
            order.setItems(items);
        }
        // 2. 统一返回格式，数据放在 data 里
        return Map.of("code", 200, "data", orders, "msg", "查询成功");
    }

    /**
     * 单个支付功能
     */
    @PostMapping("/pay")
    @Transactional
    public Map<String, Object> pay(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("id").toString());
        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setStatus(1);
            order.setPayTime(LocalDateTime.now());
            orderService.updateById(order);
            return Map.of("code", 200, "msg", "支付成功");
        }
        return Map.of("code", 500, "msg", "订单不存在");
    }

    /**
     * 一键付款（批量）
     */
    @PostMapping("/payAll")
    @Transactional // 3. 修复：补充批量操作必须的事务注解
    public Map<String, Object> payAll(@RequestBody Map<String, Object> params) {
        Object idsObj = params.get("ids");
        if (!(idsObj instanceof List)) {
            return Map.of("code", 400, "msg", "参数格式错误");
        }

        // 4. 修复：消除 Unchecked cast 警告
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) idsObj;

        for (Integer id : ids) {
            Order order = orderService.getById(id.longValue());
            if (order != null && order.getStatus() == 0) {
                order.setStatus(1);
                order.setPayTime(LocalDateTime.now()); // 5. 修复：补全遗漏的支付时间设置
                orderService.updateById(order);
            }
        }
        return Map.of("code", 200, "msg", "批量支付成功");
    }

    /**
     * 删除订单
     */
    @PostMapping("/delete")
    @Transactional
    public Map<String, Object> delete(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("id").toString());
        orderItemService.remove(new QueryWrapper<OrderItem>().eq("order_id", orderId));
        orderService.removeById(orderId);
        return Map.of("code", 200, "msg", "删除成功");
    }
}