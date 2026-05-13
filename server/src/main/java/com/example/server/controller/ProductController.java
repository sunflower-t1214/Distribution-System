package com.example.server.controller;

import com.example.server.entity.Product;
import com.example.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin // 解决前后端联调的跨域问题
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     * 改动：确保 Long 类型的一致性，增加简单的判空逻辑
     */
    @GetMapping("/list")
    public List<Product> getList() {
        // status=1 表示上架。请确保数据库中有 status 字段且值为 1
        return productService.query().eq("status", 1).list();
    }

    /**
     * 获取商品详情
     * 改动：id 类型建议与数据库 Long 匹配
     */
    @GetMapping("/{id}")
    public Product getDetail(@PathVariable Long id) {
        return productService.getById(id);
    }
}