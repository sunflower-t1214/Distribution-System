package com.example.server.controller;

import com.example.server.common.Result;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/distributor")
@CrossOrigin
public class DistributorController {

    @GetMapping("/team")
    public Result<Map<String, Object>> getTeam(@RequestParam Integer distributorId) {
        Map<String, Object> data = new HashMap<>();
        data.put("distributorId", distributorId);
        data.put("level", 2);
        data.put("directCount", 8);
        data.put("indirectCount", 23);
        data.put("totalTeamCount", 31);

        List<Map<String, Object>> members = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", 100 + i);
            m.put("name", "分销员" + (100 + i));
            m.put("level", "普通");
            m.put("orderCount", 5 + i * 2);
            m.put("contribution", new BigDecimal(100 + i * 50));
            members.add(m);
        }
        data.put("members", members);
        return Result.success(data);
    }

    @GetMapping("/earnings")
    public Result<Map<String, Object>> getEarnings(@RequestParam Integer distributorId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalCommission", new BigDecimal("3680.50"));
        data.put("withdrawable", new BigDecimal("1280.00"));
        data.put("settled", new BigDecimal("2100.50"));
        data.put("pending", new BigDecimal("580.00"));
        data.put("thisMonth", new BigDecimal("860.00"));
        data.put("lastMonth", new BigDecimal("1240.00"));

        List<Map<String, Object>> timeline = new ArrayList<>();
        String[] labels = {"1月", "2月", "3月", "4月", "5月"};
        String[] amounts = {"520", "680", "1240", "860", "480"};
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("month", labels[i]);
            m.put("amount", new BigDecimal(amounts[i]));
            timeline.add(m);
        }
        data.put("monthlyTimeline", timeline);
        return Result.success(data);
    }

    @PostMapping("/audit")
    public Result<String> audit(@RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("userId");
        Integer auditStatus = (Integer) body.get("auditStatus");
        String result = auditStatus == 1 ? "分销员审核通过" : "分销员申请已拒绝";
        return Result.success("用户 " + userId + " " + result);
    }

    @PostMapping("/withdraw")
    public Result<Map<String, Object>> applyWithdraw(@RequestBody Map<String, Object> body) {
        Integer distributorId = (Integer) body.get("distributorId");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String accountType = (String) body.getOrDefault("accountType", "wechat");

        Map<String, Object> result = new HashMap<>();
        result.put("applyId", new Random().nextInt(99999));
        result.put("distributorId", distributorId);
        result.put("amount", amount);
        result.put("accountType", accountType);
        result.put("status", "待审核");
        result.put("applyTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 19));

        return Result.success(result);
    }
}
