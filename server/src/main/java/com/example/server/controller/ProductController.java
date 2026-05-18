package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.common.Result;
import com.example.server.entity.Product;
import com.example.server.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CACHE_KEY = "cache:products";
    private static final long CACHE_TTL = 30;

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
        p.getRecords().forEach(this::enrichSharingReward);
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
        String cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            try {
                CollectionType type = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, Product.class);
                List<Product> list = objectMapper.readValue(cached, type);
                list.forEach(this::enrichSharingReward);
                return list;
            } catch (Exception e) {
                redisTemplate.delete(CACHE_KEY);
            }
        }
        List<Product> list = productService.lambdaQuery().eq(Product::getStatus, 1).list();
        list.forEach(this::enrichSharingReward);
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(list),
                    CACHE_TTL, TimeUnit.MINUTES);
        } catch (JsonProcessingException ignored) {}
        return list;
    }

    @PostMapping("/refresh-cache")
    public Result<String> refreshCache() {
        List<Product> list = productService.lambdaQuery().eq(Product::getStatus, 1).list();
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(list),
                    CACHE_TTL, TimeUnit.MINUTES);
        } catch (JsonProcessingException ignored) {}
        return Result.success("缓存已刷新，共 " + list.size() + " 条");
    }

    @GetMapping("/{id}")
    public Product getDetail(@PathVariable Long id) {
        Product p = productService.getById(id);
        if (p != null) enrichSharingReward(p);
        return p;
    }

    private void enrichSharingReward(Product p) {
        if (p.getPrice() != null && p.getCommissionRate() != null) {
            BigDecimal reward = p.getPrice().multiply(p.getCommissionRate())
                    .setScale(2, RoundingMode.HALF_UP);
            p.setSharingReward(reward);
        } else {
            p.setSharingReward(BigDecimal.ZERO.setScale(2));
        }
    }
}
