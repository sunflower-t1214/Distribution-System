package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.entity.Order;
import com.example.server.entity.Sales;
import com.example.server.entity.User;
import com.example.server.service.OrderService;
import com.example.server.service.SalesService;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    // 生成推广链接
    @GetMapping("/getShareLink")
    public Map<String, Object> getShareLink(@RequestParam Long salesId) {
        Sales sales = salesService.getById(salesId);
        Map<String, Object> res = new HashMap<>();
        if (sales == null) {
            res.put("code", 500);
            res.put("msg", "销售员不存在");
            return res;
        }

        if (sales.getShareCode() == null || sales.getShareCode().isEmpty()) {
            String code = UUID.randomUUID().toString().replace("-", "");
            sales.setShareCode(code);
            salesService.updateById(sales);
        }

        String link = "http://localhost:5173/pages/home/home?shareCode=" + sales.getShareCode();
        res.put("code", 200);
        res.put("shareLink", link);
        res.put("shareCode", sales.getShareCode());
        return res;
    }

    // 绑定分销关系
    @PostMapping("/bind")
    public Map<String, Object> bind(@RequestParam String shareCode, @RequestParam Integer userId) {
        QueryWrapper<Sales> wrapper = new QueryWrapper<>();
        wrapper.eq("share_code", shareCode);
        Sales sales = salesService.getOne(wrapper);

        Map<String, Object> res = new HashMap<>();
        if (sales == null) {
            res.put("code", 500);
            res.put("msg", "推广码无效");
            return res;
        }

        User user = userService.getById(Long.valueOf(userId));
        if (user == null) {
            res.put("code", 500);
            res.put("msg", "用户不存在");
            return res;
        }

        user.setSalesId(sales.getSalesId().intValue());
        userService.updateById(user);

        res.put("code", 200);
        res.put("msg", "绑定成功");
        return res;
    }

    // 推广统计
    @GetMapping("/statistics")
    public Map<String, Object> statistics(@RequestParam Long salesId) {
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("sales_id", salesId).eq("status", 1);
        long count = orderService.count(orderQuery);

        BigDecimal total = new BigDecimal("0");
        try {
            Order order = orderService.query()
                    .eq("sales_id", salesId)
                    .eq("status", 1)
                    .select("sum(total_amount) as total_amount")
                    .one();
            if (order != null && order.getTotalAmount() != null) {
                total = order.getTotalAmount();
            }
        } catch (Exception e) {}

        BigDecimal commission = total.multiply(new BigDecimal("0.1"));

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("orderCount", count);
        res.put("totalSales", total);
        res.put("commission", commission);
        return res;
    }
}