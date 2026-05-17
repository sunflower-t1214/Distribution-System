package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.common.Result;
import com.example.server.entity.Product;
import com.example.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public Result<Page<Product>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String category) {
        Page<Product> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        wrapper.orderByDesc(Product::getProductId);
        productService.page(p, wrapper);
        return Result.success(p);
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getCategories() {
        List<Product> all = productService.lambdaQuery().eq(Product::getStatus, 1).list();
        Map<String, Long> grouped = all.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(e -> Map.<String, Object>of("name", e.getKey(), "count", e.getValue()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("/list")
    public List<Product> getList() {
        return productService.lambdaQuery().eq(Product::getStatus, 1).list();
    }

    @GetMapping("/{id}")
    public Product getDetail(@PathVariable Long id) {
        return productService.getById(id);
    }
}
