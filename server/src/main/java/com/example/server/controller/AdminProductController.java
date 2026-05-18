package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.common.Result;
import com.example.server.entity.Product;
import com.example.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        Product p = productService.getById(id);
        if (p == null) return Result.fail("商品不存在");
        p.setStatus(status);
        productService.updateById(p);
        return Result.success(status == 1 ? "已上架" : "已下架");
    }

    @PutMapping("/{id}/price")
    public Result<String> updatePrice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BigDecimal price = new BigDecimal(body.get("price").toString());
        Product p = productService.getById(id);
        if (p == null) return Result.fail("商品不存在");
        p.setPrice(price);
        productService.updateById(p);
        return Result.success("价格已更新为 ¥" + price);
    }

    @PutMapping("/{id}/commission-rate")
    public Result<String> updateCommissionRate(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BigDecimal rate = new BigDecimal(body.get("commissionRate").toString());
        Product p = productService.getById(id);
        if (p == null) return Result.fail("商品不存在");
        p.setCommissionRate(rate);
        productService.updateById(p);
        return Result.success("佣金比例已更新为 " + rate + "%");
    }

    @PutMapping("/{id}/commission")
    public Result<String> updateCommission(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return updateCommissionRate(id, body);
    }

    @PostMapping("/batch-status")
    public Result<String> batchStatus(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        java.util.List<Integer> ids = (java.util.List<Integer>) body.get("ids");
        Integer status = (Integer) body.get("status");
        productService.update(new UpdateWrapper<Product>()
                .in("product_id", ids).set("status", status));
        return Result.success((status == 1 ? "上架" : "下架") + ids.size() + "件商品成功");
    }
}
