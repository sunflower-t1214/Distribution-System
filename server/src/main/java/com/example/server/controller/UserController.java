package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.server.common.JwtUtils;
import com.example.server.common.Result;
import com.example.server.entity.User;
import com.example.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String phone = (String) body.get("phone");
        String password = (String) body.get("password");
        Object inviteCodeObj = body.get("inviteCode");

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRegisterTime(LocalDateTime.now());

        if (inviteCodeObj != null) {
            Integer inviteCode = Integer.valueOf(inviteCodeObj.toString());
            Integer inviterId = inviteCode - 10000;
            if (inviterId > 0) {
                User inviter = userMapper.selectById(inviterId);
                if (inviter != null && !inviterId.equals(user.getUserId())) {
                    user.setInviterId(inviterId);
                }
            }
        }

        int insert = userMapper.insert(user);
        if (insert > 0) {
            user.setPassword(null);
            String token = JwtUtils.generateToken(user.getUserId());
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            return Result.success(data);
        }
        return Result.fail("注册失败");
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User loginParam) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginParam.getPhone());

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) return Result.fail("手机号或密码错误");

        boolean matched;
        String storedPwd = user.getPassword();
        if (storedPwd != null && storedPwd.startsWith("$2a$")) {
            matched = passwordEncoder.matches(loginParam.getPassword(), storedPwd);
        } else {
            matched = loginParam.getPassword() != null && loginParam.getPassword().equals(storedPwd);
            if (matched) {
                user.setPassword(passwordEncoder.encode(storedPwd));
                userMapper.updateById(user);
            }
        }

        if (matched) {
            user.setPassword(null);
            String token = JwtUtils.generateToken(user.getUserId());
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            return Result.success(data);
        } else {
            return Result.fail("手机号或密码错误");
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Integer id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }

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
