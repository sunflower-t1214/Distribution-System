package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.entity.User;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    private static final Map<String, CodeEntry> codeMap = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();
    private static final long EXPIRE_MS = 5 * 60 * 1000;

    @GetMapping("/code")
    public Result<?> getCode(@RequestParam String phone) {
        String code = String.format("%06d", RANDOM.nextInt(999999));
        codeMap.put(phone, new CodeEntry(code, System.currentTimeMillis() + EXPIRE_MS));
        System.out.println("【系统日志】当前生成的动态验证码为：" + code);
        return Result.success("验证码已发送");
    }

    @PostMapping("/verify")
    public Result<?> verify(@RequestBody Map<String, Object> params) {
        String phone = (String) params.get("phone");
        String code = (String) params.get("code");
        String role = (String) params.get("role");

        CodeEntry entry = codeMap.get(phone);
        if (entry == null) {
            return Result.fail("请先获取验证码");
        }
        if (System.currentTimeMillis() > entry.expireTime) {
            codeMap.remove(phone);
            return Result.fail("验证码已过期，请重新获取");
        }
        if (!entry.code.equals(code)) {
            return Result.fail("验证码错误");
        }

        codeMap.remove(phone);

        User user = userService.query().eq("phone", phone).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setRole(role);
        userService.updateById(user);

        System.out.println("【系统日志】用户 " + phone + " 身份已切换为: " + role);

        return Result.success("身份切换成功");
    }

    private static class CodeEntry {
        String code;
        long expireTime;
        CodeEntry(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
