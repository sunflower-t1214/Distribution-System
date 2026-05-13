package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.Product;
import com.example.server.mapper.ProductMapper;
import com.example.server.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * 商品业务逻辑实现类
 */
@Service  // 👈 核心：告诉 Spring 这个类是供别人调用的 Bean
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    // 继承 ServiceImpl 后，你就自动实现了 IService 里的所有方法
}