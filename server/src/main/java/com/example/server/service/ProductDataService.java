package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.Product;
import com.example.server.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProductDataService extends ServiceImpl<ProductMapper, Product> {

    private static final Random RANDOM = new Random();
    private static final int TOTAL = 1000;

    private static final String[][] CATEGORIES = {
        {"数码/手机", "科技数码", "tech"},
        {"电脑/办公", "电脑办公", "computer"},
        {"家电/电器", "家用电器", "appliance"},
        {"美妆/个护", "美妆护肤", "beauty"},
        {"食品/生鲜", "食品生鲜", "food"},
        {"家居/日用", "家居日用", "interior"},
        {"服饰/鞋包", "服饰鞋包", "fashion"},
        {"运动/户外", "运动户外", "sport"},
        {"母婴/玩具", "母婴玩具", "baby"}
    };

    private static final String[] BRANDS = {
        "华为", "苹果", "小米", "三星", "OPPO", "vivo", "联想", "戴尔", "惠普",
        "海尔", "格力", "美的", "苏泊尔", "飞利浦", "松下", "索尼", "佳能", "尼康",
        "耐克", "阿迪达斯", "安踏", "李宁", "雅诗兰黛", "兰蔻", "欧莱雅", "百草味",
        "良品铺子", "三只松鼠", "蒙牛", "伊利"
    };

    private static final String[] ADJECTIVES = {
        "旗舰款", "Pro Max", "轻奢版", "经典款", "升级版", "豪华装",
        "家庭装", "便携款", "智能版", "高端款", "限量版", "超值装"
    };

    public String runPipeline() {
        List<Product> batch = new ArrayList<>(TOTAL);
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < TOTAL; i++) {
            try {
                Product p = generateProduct(i);
                String seed = getSeed(p.getCategory()) + i;
                p.setImageUrl("https://picsum.photos/seed/" + seed + "/800/800");
                batch.add(p);
                successCount++;
                if ((i + 1) % 100 == 0) {
                    System.out.println("【流水线】已生成 " + (i + 1) + " / " + TOTAL + " 条");
                }
            } catch (Exception e) {
                failCount++;
                System.err.println("【流水线】第 " + (i + 1) + " 条生成失败: " + e.getMessage());
            }
        }

        if (!batch.isEmpty()) {
            saveBatch(batch, 100);
        }

        return String.format("流水线完成：成功 %d 条，失败 %d 条，共写入 %d 条", successCount, failCount, batch.size());
    }

    private Product generateProduct(int index) {
        Product p = new Product();
        int catIndex = RANDOM.nextInt(CATEGORIES.length);
        String[] cat = CATEGORIES[catIndex];
        String brand = BRANDS[RANDOM.nextInt(BRANDS.length)];
        String adj = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];

        p.setName(brand + " " + cat[1] + adj);
        p.setCategory(cat[0]);
        p.setPrice(BigDecimal.valueOf(10 + RANDOM.nextDouble() * 9989).setScale(2, RoundingMode.HALF_UP));
        p.setStock(10 + RANDOM.nextInt(491));
        p.setStatus(1);
        p.setCommissionRate(BigDecimal.valueOf(1 + RANDOM.nextDouble() * 14).setScale(2, RoundingMode.HALF_UP));
        p.setDescription(brand + "旗下" + cat[1] + adj + "，高清大图展示，品质保障，极速发货。");
        return p;
    }

    private String getSeed(String category) {
        for (String[] cat : CATEGORIES) {
            if (cat[0].equals(category)) return cat[2];
        }
        return "product";
    }
}
