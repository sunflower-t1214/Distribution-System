package com.example.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.server.common.Result;
import com.example.server.common.UserContext;
import com.example.server.entity.User;
import com.example.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/distribution")
@CrossOrigin
public class DistributorBindController {

    @Autowired
    private UserService userService;

    @GetMapping("/my-info")
    public Result<Map<String, Object>> myInfo() {
        Integer userId = UserContext.getUserId();
        User me = userService.getById(userId);
        if (me == null) return Result.fail("用户不存在");

        int inviteCode = me.getUserId() + 10000;

        List<User> invitedList = userService.lambdaQuery()
                .eq(User::getInviterId, userId)
                .list();

        long teamCount = invitedList.size();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", me.getUserId());
        data.put("name", me.getName());
        data.put("commissionBalance", me.getCommissionBalance() != null ? me.getCommissionBalance() : "0.00");
        data.put("inviteCode", inviteCode);
        data.put("teamCount", teamCount);
        // 二级伙伴（一级伙伴邀请的人）
        List<Integer> l1Ids = invitedList.stream().map(User::getUserId).toList();
        long l2Count = 0;
        if (!l1Ids.isEmpty()) {
            l2Count = userService.lambdaQuery().in(User::getInviterId, l1Ids).count();
        }

        data.put("level1Count", invitedList.size());
        data.put("level2Count", l2Count);
        data.put("invitedList", invitedList.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("name", u.getName());
            m.put("phone", u.getPhone());
            m.put("registerTime", u.getRegisterTime() != null ? u.getRegisterTime().toString() : "");
            return m;
        }));

        return Result.success(data);
    }

    @PostMapping("/bind")
    public Result<String> bind(@RequestBody Map<String, Object> params) {
        Integer userId = UserContext.getUserId();
        Integer inviteCode = (Integer) params.get("inviteCode");

        if (inviteCode == null) return Result.fail("邀请码不能为空");

        Integer inviterId = inviteCode - 10000;
        if (inviterId <= 0) return Result.fail("邀请码无效");

        User me = userService.getById(userId);
        if (me == null) return Result.fail("用户不存在");

        if (me.getInviterId() != null) return Result.fail("您已有上级，无法重复绑定");

        if (inviterId.equals(userId)) return Result.fail("不能邀请自己");

        User inviter = userService.getById(inviterId);
        if (inviter == null) return Result.fail("邀请人不存在");

        me.setInviterId(inviterId);
        userService.updateById(me);

        System.out.println("【师徒关系】" + me.getName() + " 成为 " + inviter.getName() + " 的下级");
        return Result.success("绑定成功，欢迎成为 " + inviter.getName() + " 的团队成员");
    }

    @GetMapping("/team")
    public Result<Map<String, Object>> getTeam(@RequestParam(defaultValue = "1") int level) {
        Integer userId = UserContext.getUserId();

        List<User> level1 = userService.lambdaQuery()
                .eq(User::getInviterId, userId)
                .list();

        List<Map<String, Object>> level2 = new ArrayList<>();
        if (level >= 2) {
            List<Integer> l1Ids = level1.stream().map(User::getUserId).toList();
            if (!l1Ids.isEmpty()) {
                List<User> l2Users = userService.lambdaQuery()
                        .in(User::getInviterId, l1Ids)
                        .list();
                for (User u : l2Users) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("userId", u.getUserId());
                    m.put("name", u.getName());
                    m.put("phone", u.getPhone());
                    m.put("registerTime", u.getRegisterTime() != null ? u.getRegisterTime().toString() : "");
                    // 查找上级
                    User parent = level1.stream().filter(l1 -> l1.getUserId().equals(u.getInviterId())).findFirst().orElse(null);
                    m.put("inviterName", parent != null ? parent.getName() : "未知");
                    level2.add(m);
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("level1", level1.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("name", u.getName());
            m.put("phone", u.getPhone());
            m.put("registerTime", u.getRegisterTime() != null ? u.getRegisterTime().toString() : "");
            return m;
        }));
        data.put("level2", level2);
        data.put("level1Count", level1.size());
        data.put("level2Count", level2.size());
        return Result.success(data);
    }
}
