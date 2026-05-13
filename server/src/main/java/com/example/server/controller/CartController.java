package com.example.server.controller;

import com.example.server.entity.Cart;
import com.example.server.entity.Product;
import com.example.server.service.CartService;
import com.example.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    /**
     * 获取购物车列表
     * produces = "application/json" 强制指定返回 JSON，防止浏览器将其误认为图片
     */
    @GetMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public List<Cart> list(@RequestParam Long userId) {
        System.out.println("收到查询请求，用户ID: " + userId);

        // 1. 查询基础记录
        List<Cart> carts = cartService.query().eq("user_id", userId).list();
        System.out.println("数据库查到记录条数: " + (carts != null ? carts.size() : 0));

        if (carts != null) {
            // 2. 补全商品详情
            for (Cart cart : carts) {
                Product p = productService.getById(cart.getProductId());
                if (p != null) {
                    cart.setProductName(p.getName());
                    cart.setProductPrice(p.getPrice());
                    cart.setProductImageUrl(p.getImageUrl());
                }
            }
        }
        return carts;
    }

    @PostMapping("/add")
    public String add(@RequestBody Cart cart) {
        Cart exist = cartService.query()
                .eq("user_id", cart.getUserId())
                .eq("product_id", cart.getProductId())
                .one();

        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + cart.getQuantity());
            cartService.updateById(exist);
        } else {
            cartService.save(cart);
        }
        return "添加成功";
    }

    @PostMapping("/delete")
    public String delete(@RequestBody Cart cart) {
        // 这里的 cart 对象里通常只需要传个 id 进来
        cartService.removeById(cart.getId());
        return "删除成功";
    }
}