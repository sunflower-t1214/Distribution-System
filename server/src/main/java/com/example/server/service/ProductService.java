package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.entity.Product;

/**
 * 商品业务逻辑接口
 */
public interface ProductService extends IService<Product> {
    // 继承 IService 后，你就自动拥有了查询、删除、更新等基础功能
}