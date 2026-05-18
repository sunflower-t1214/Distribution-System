package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.common.Result;
import com.example.server.common.UserContext;
import com.example.server.entity.Cart;
import com.example.server.entity.Order;
import com.example.server.entity.OrderItem;
import com.example.server.entity.Product;
import com.example.server.entity.User;
import com.example.server.service.CartService;
import com.example.server.service.OrderItemService;
import com.example.server.service.OrderService;
import com.example.server.service.ProductService;
import com.example.server.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final ProductService productService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    private static final String STOCK_PREFIX = "stock:product:";

    @Autowired
    public OrderController(OrderService orderService, CartService cartService,
                           OrderItemService orderItemService, UserService userService,
                           ProductService productService,
                           RedissonClient redissonClient,
                           StringRedisTemplate redisTemplate) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.productService = productService;
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 创建订单（Redisson 分布式锁 + Redis 库存防超卖）
     */
    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return Map.of("code", 400, "msg", "商品列表不能为空");
        }

        BigDecimal total = BigDecimal.ZERO;

        // ── 分布式锁：按每个商品独立加锁 ──
        List<RLock> locks = order.getItems().stream()
                .map(item -> redissonClient.getLock("lock:product:" + item.getProductId()))
                .collect(java.util.stream.Collectors.toList());

        // 按固定顺序加锁（防止死锁）
        locks.sort((a, b) -> a.getName().compareTo(b.getName()));

        try {
            for (RLock lock : locks) {
                if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                    return Map.of("code", 500, "msg", "系统繁忙，请稍后再试");
                }
            }

            for (OrderItem item : order.getItems()) {
                Product product = productService.getById(item.getProductId());
                if (product == null) {
                    return Map.of("code", 400, "msg", "商品 " + item.getProductId() + " 不存在");
                }

                // ── Redis 库存校验 ──
                String stockKey = STOCK_PREFIX + item.getProductId();
                String redisStock = redisTemplate.opsForValue().get(stockKey);
                int stock;
                if (redisStock != null) {
                    stock = Integer.parseInt(redisStock);
                } else {
                    stock = product.getStock();
                    redisTemplate.opsForValue().set(stockKey, String.valueOf(stock));
                }

                if (stock < item.getQuantity()) {
                    return Map.of("code", 400, "msg", "商品「" + product.getName() + "」库存不足");
                }

                // ── 扣减 Redis 库存 ──
                redisTemplate.opsForValue().decrement(stockKey, item.getQuantity());

                // ── 扣减 MySQL 库存 ──
                product.setStock(product.getStock() - item.getQuantity());
                productService.updateById(product);

                if (item.getProductPrice() == null) {
                    item.setProductPrice(product.getPrice());
                }
                total = total.add(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Map.of("code", 500, "msg", "获取锁中断");
        } finally {
            locks.forEach(RLock::unlock);
        }

        Integer userId = UserContext.getUserId();
        order.setUserId(userId.longValue());
        order.setOrderSn(UUID.randomUUID().toString().replace("-", ""));
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setTotalAmount(total);
        order.setPayAmount(total);

        User user = userService.getById(userId);
        if (user != null && user.getSalesId() != null) {
            order.setSalesId(user.getSalesId().longValue());
        }

        orderService.save(order);

        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getId());
            orderItemService.save(item);
            cartService.remove(new QueryWrapper<Cart>()
                    .eq("user_id", userId)
                    .eq("product_id", item.getProductId()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "下单成功");
        result.put("orderId", order.getId());
        return result;
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Long userId = UserContext.getUserId().longValue();
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

            // ── 异步解耦：发消息到 Redis 阻塞队列，Consumer 后台算佣金 ──
            redissonClient.<String>getBlockingQueue("queue:commission").offer(String.valueOf(orderId));

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