package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.server.common.Result;
import com.example.server.entity.User;
import com.example.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 1. 用户注册接口
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        int insert = userMapper.insert(user);
        if (insert > 0) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("注册失败");
    }

    /**
     * 2. 用户登录接口
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginParam) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginParam.getPhone())
                .eq(User::getPassword, loginParam.getPassword());

        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        } else {
            return Result.fail("手机号或密码错误");
        }
    }

    /**
     * 3. 简化版获取用户信息（直接用用户ID，避免Servlet依赖）
     * 前端调用：/user/info?id=xxx
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Integer id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }

    /**
     * 4. 备用：按ID路径查询
     */
    @GetMapping("/info/{id}")
    public Result<User> getInfoById(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }
}