package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.Product;
import com.example.server.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductDataService extends ServiceImpl<ProductMapper, Product> {

    private static final Random RANDOM = new Random();
    private static final int TOTAL = 1000;
    private static final String IMG_CDN = "https://picsum.photos/seed/";

    // ── 核心商品字典 ──
    private static final Map<String, String[]> PRODUCT_DICT = new LinkedHashMap<>();
    // ── 品类图池（每类 6 张，产品按 index % 6 取模） ──
    private static final Map<String, String[]> IMAGE_POOL = new LinkedHashMap<>();
    // ── 品类佣金率区间 ──
    private static final Map<String, double[]> COMMISSION_RATES = new LinkedHashMap<>();

    static {
        PRODUCT_DICT.put("数码/手机", new String[]{
            "iPhone 15 Pro Max 256GB 原色钛金属", "华为 Mate 60 Pro 昆仑玻璃版 12+512G",
            "小米14 Ultra 骁龙8 Gen3 徕卡光学", "OPPO Find X7 Ultra 哈苏人像大师",
            "vivo X100 Pro 蔡司超级长焦 蓝海电池", "三星 Galaxy S24 Ultra 钛金属框",
            "荣耀 Magic6 Pro 鹰眼相机 青海湖电池", "一加 12 第三代骁龙8 2K东方屏",
            "iQOO 12 电竞芯片Q1 超分超帧", "Redmi K70 Pro 第二代高端2K中国屏",
            "华为 P60 Art 双向北斗卫星消息", "魅族 21 三星直屏 骁龙8 Gen3",
            "小米 14 徕卡光学 Summilux 镜头", "华为 Nova 12 Ultra 前置6000万",
            "Apple Watch Ultra 2 钛金属版", "三星 Galaxy Watch6 Classic 旋转表圈",
            "华为 FreeBuds Pro 3 麒麟A2芯片", "索尼 WF-1000XM5 主动降噪旗舰",
            "iPad Air M2 11英寸 Liquid 视网膜", "任天堂 Switch OLED 马力欧红蓝"
        });
        PRODUCT_DICT.put("电脑/办公", new String[]{
            "Apple MacBook Air M3 15英寸", "联想 ThinkPad X1 Carbon Gen 12",
            "华为 MateBook X Pro 酷睿Ultra", "戴尔 XPS 16 英特尔酷睿Ultra 9",
            "ROG 枪神8 Plus 超竞版 14代i9", "小米 Book Pro 16 2024 OLED",
            "惠普 星Book Pro 14 锐龙版", "微软 Surface Pro 10 商用版",
            "联想 小新Pro 16 AI超能本", "华硕 灵耀14 双屏 OLED",
            "Apple iMac M3 24英寸 一体机", "华为 MateStation X 一体机 4K+",
            "戴尔 U2724D 4K 专业显示器", "明基 RD280U 编程护眼屏",
            "罗技 MX Keys 无线键盘 多设备", "罗技 G Pro X Superlight 2 游戏鼠标",
            "西部数据 SN850X 2TB 固态硬盘", "三星 T7 Shield 2TB 移动固态",
            "华为 坤灵 S110 8口交换机", "绿联 雷电4 扩展坞 8K高清"
        });
        PRODUCT_DICT.put("家电/电器", new String[]{
            "海尔 全空间保鲜冰箱 500升", "美的 一级变频空调 1.5匹 新风",
            "格力 云佳 1.5匹 新一级能效", "TCL 75英寸 Mini LED 电视 Q10H",
            "海信 85英寸 ULED X 电视 U8H", "戴森 V15 Detect 无线吸尘器",
            "石头 P10 Pro 自清洁扫拖机器人", "科沃斯 X2 COMBO 扫拖一体",
            "方太 集成烹饪中心 JCD10C", "老板 60D1S 侧吸油烟机 变频",
            "史密斯 60升 免更换镁棒 热水器", "松下 全自动洗衣机 10公斤 泡沫净",
            "小米 米家 洗烘一体机 10公斤", "美的 微晶口感 冰箱 508升",
            "格力 晶弘 深冷速冻 冰箱 520升", "飞利浦 空气炸锅 7.3L 可视窗口",
            "九阳 0涂层 电饭煲 4L 不锈钢内胆", "小米 空气净化器 4 Pro HEPA滤芯",
            "添可 芙万4.0 洗地机 恒压活水", "科沃斯 T20 PRO 扫拖 热水洗拖布"
        });
        PRODUCT_DICT.put("美妆/个护", new String[]{
            "雅诗兰黛 小棕瓶精华 第七代 100ml", "兰蔻 小黑瓶 肌底精华液 115ml",
            "海蓝之谜 经典面霜 60ml 修护", "赫莲娜 黑绷带 面霜 50ml 玻色因",
            "SK-II 神仙水 230ml 精华液", "资生堂 红腰子 精华 100ml 肌活",
            "香奈儿 五号 香水 经典淡香 100ml", "迪奥 花漾甜心 香水 50ml",
            "汤姆福特 TF 黑管 口红 16# SCARLET", "YSL 小金条 口红 21# 复古红",
            "卡诗 黑钻 护发精油 120ml 修护", "戴森 Airwrap 多功能美发器",
            "飞利浦 电动牙刷 HX9352 钻石亮白", "松下 电吹风 EH-NA9C 纳米水离子",
            "海飞丝 去屑洗发水 止痒呵护 400ml", "飘柔 精油护理 洗发水 500ml",
            "潘婷 3分钟奇迹 护发素 170ml", "清扬 男士去屑 洗发露 500ml",
            "欧莱雅 紫熨斗 眼霜 30ml 玻色因", "珀莱雅 双抗精华 2.0 抗氧化"
        });
        PRODUCT_DICT.put("食品/生鲜", new String[]{
            "三只松鼠 每日坚果 750g 礼盒装", "良品铺子 肉类零食大礼包 1000g",
            "百草味 坚果大礼包 年货 1528g", "农夫山泉 天然矿泉水 550ml×24瓶",
            "元气森林 苏打气泡水 白桃 330ml×12", "蒙牛 特仑苏 纯牛奶 250ml×16盒",
            "伊利 金典 有机纯牛奶 250ml×12盒", "认养一头牛 纯牛奶 200ml×24盒",
            "五常大米 稻花香2号 5kg 有机", "十月稻田 长粒香米 5kg 东北大米",
            "金龙鱼 葵花籽油 5L 物理压榨", "智利 3J 车厘子 2.5kg 空运新鲜",
            "佳沛 金奇异果 大果 12粒 新西兰", "泰国 金枕头 榴莲 3-4斤 冷冻",
            "阳澄湖 大闸蟹 礼盒 8只 公4.5两", "大连 海参 淡干 50g 即食辽参",
            "京东 自营 鲜鸡蛋 30枚 谷物喂养", "思念 水饺 猪肉白菜 1000g 2斤装",
            "湾仔码头 玉米蔬菜水饺 720g", "好欢螺 螺蛳粉 加辣加臭 400g×3"
        });
        PRODUCT_DICT.put("家居/日用", new String[]{
            "蓝月亮 洗衣液 薰衣草香 3kg×2瓶", "立白 洗衣凝珠 樱花香 52颗装",
            "舒肤佳 沐浴露 纯白清香 720ml", "海飞丝 去屑洗发水 400ml 止痒",
            "全棉时代 纯棉柔巾 80抽×12包", "维达 抽纸 超韧 3层 130抽×24包",
            "公牛 魔方插座 USB 3USB+3插孔", "小米 智能灯泡 色温调节 彩光版",
            "网易严选 除湿盒 吸湿防霉 500ml×6", "乐扣乐扣 保鲜盒 10件套 微波炉可用",
            "双立人 刀具 7件套 德国不锈钢", "苏泊尔 不粘锅 炒锅 30cm 少油烟",
            "水星家纺 四件套 纯棉 简约 1.8m", "公牛 插排 8位总控 全长5米",
            "欧普 护眼台灯 LED 国AA级 无频闪", "小米 自动洗手液机 感应出泡",
            "金稻 直发梳 负离子 护发直发器", "飞科 剃须刀 电动 FS968 全身水洗",
            "美的 烧水壶 1.7L 304不锈钢", "苏泊尔 保温杯 316L不锈钢 500ml"
        });
        PRODUCT_DICT.put("服饰/鞋包", new String[]{
            "Nike Air Force 1 '07 纯白 空军一号", "Adidas Ultraboost Light 跑鞋",
            "安踏 KT9 汤普森 签名篮球鞋", "李宁 赤兔7 Pro 马拉松 跑鞋",
            "北面 1996 经典款 羽绒服 保暖", "波司登 极寒系列 羽绒服 加厚",
            "优衣库 摇粒绒 拉链夹克 长袖", "ZARA 双面穿 飞行员夹克 棉服",
            "LV Neverfull 中号 托特包 老花", "古驰 GG Marmont 链条包 小号",
            "COACH 奥莱 托特包 皮革 C字印花", "卡西欧 G-Shock DW-5600 经典款",
            "浪琴 康卡斯 潜水表 自动机械 41mm", "雷朋 RB3025 飞行员 太阳镜 偏光",
            "爱步 商务正装鞋 男 欧文 系带", "百丽 女短靴 切尔西 粗跟 弹力靴",
            "维多利亚的秘密 蕾丝文胸 聚拢", "蕉内 热皮 保暖内衣 男女 加绒",
            "海澜之家 衬衫 免烫 长袖 男", "UGG 经典短靴 雪地靴 女 防滑"
        });
        PRODUCT_DICT.put("运动/户外", new String[]{
            "华为 WATCH GT 4 46mm 运动智能", "小米手环 9 NFC 全天心率监测",
            "YONEX 天斧99 羽毛球拍 专业级", "蝴蝶 Viscaria 乒乓球拍 金标",
            "斯伯丁 篮球 7号 传奇系列 室内外", "迪卡侬 MH100 冲锋衣 男女 防水",
            "探路者 登山鞋 男 中帮 GTX防水", "始祖鸟 Gamma MX 软壳衣 防风",
            "牧高笛 帐篷 冷山3 双人 三季帐", "黑钻 越野登山杖 铝合金 伸缩",
            "欧克利 运动太阳镜 OO9208 偏光", "妙界 R3 筋膜枪 按摩枪 深层放松",
            "野小兽 动感单车 家用 磁控静音", "HEAD 海德 滑雪板 全能板 套装",
            "Brompton 小布 C Line 折叠自行车", "捷安特 ATX 830 山地车 27.5寸",
            "九号 MMAX 110P 电动自行车", "Keep 瑜伽垫 加厚 防滑 双面",
            "The North Face 双肩包 户外 38L", "骆驼 露营天幕 防晒 防雨 六角"
        });
        PRODUCT_DICT.put("母婴/玩具", new String[]{
            "飞鹤 星飞帆 奶粉 3段 900g×2罐", "美赞臣 蓝臻 奶粉 3段 800g",
            "a2 至初 奶粉 3段 900g 新西兰", "君乐宝 旗帜 奶粉 3段 800g",
            "大王 光羽 纸尿裤 L54片 尤妮佳", "babycare 皇室系列 纸尿裤 XL30片",
            "花王 妙而舒 纸尿裤 L58片 日本版", "贝亲 宽口径 PPSU 奶瓶 240ml",
            "新安怡 电动吸奶器 SCF303 双边", "全棉时代 婴儿棉柔巾 100抽×24包",
            "童泰 新生儿连体衣 纯棉 秋冬款", "好孩子 婴儿推车 可坐可躺 GB101",
            "可优比 恒温调奶器 1.3L 不锈钢", "乐高 经典创意 大盒装 11030 积木",
            "迪士尼 安娜公主 艾莎 魔法冰雪奇缘", "泡泡玛特 SP 温度系列 盲盒 整盒",
            "Jellycat 邦尼兔 毛绒公仔 31cm", "奥迪双钻 超级飞侠 变形机器人",
            "babycare 儿童安全座椅 360旋转", "小白熊 蒸汽消毒锅 奶瓶消毒器"
        });

        // ── 品类图池（每个品类 6 张种子图） ──
        IMAGE_POOL.put("数码/手机", arr("phonep1","phonep2","phonep3","phonep4","phonep5","phonep6"));
        IMAGE_POOL.put("电脑/办公", arr("compp1","compp2","compp3","compp4","compp5","compp6"));
        IMAGE_POOL.put("家电/电器", arr("applp1","applp2","applp3","applp4","applp5","applp6"));
        IMAGE_POOL.put("美妆/个护", arr("cosmp1","cosmp2","cosmp3","cosmp4","cosmp5","cosmp6"));
        IMAGE_POOL.put("食品/生鲜", arr("foodp1","foodp2","foodp3","foodp4","foodp5","foodp6"));
        IMAGE_POOL.put("家居/日用", arr("homep1","homep2","homep3","homep4","homep5","homep6"));
        IMAGE_POOL.put("服饰/鞋包", arr("fashp1","fashp2","fashp3","fashp4","fashp5","fashp6"));
        IMAGE_POOL.put("运动/户外", arr("sprtp1","sprtp2","sprtp3","sprtp4","sprtp5","sprtp6"));
        IMAGE_POOL.put("母婴/玩具", arr("babyp1","babyp2","babyp3","babyp4","babyp5","babyp6"));

        // ── 品类佣金率：高客单低佣金，低客单高佣金 ──
        COMMISSION_RATES.put("数码/手机", new double[]{0.01, 0.02});
        COMMISSION_RATES.put("电脑/办公", new double[]{0.01, 0.02});
        COMMISSION_RATES.put("家电/电器", new double[]{0.02, 0.04});
        COMMISSION_RATES.put("美妆/个护", new double[]{0.10, 0.15});
        COMMISSION_RATES.put("食品/生鲜", new double[]{0.10, 0.15});
        COMMISSION_RATES.put("家居/日用", new double[]{0.05, 0.10});
        COMMISSION_RATES.put("服饰/鞋包", new double[]{0.05, 0.08});
        COMMISSION_RATES.put("运动/户外", new double[]{0.03, 0.06});
        COMMISSION_RATES.put("母婴/玩具", new double[]{0.04, 0.06});
    }

    private static String[] arr(String... a) { return a; }

    public String runPipeline() {
        List<Product> batch = new ArrayList<>(TOTAL);
        int idx = 0;
        List<String> catNames = new ArrayList<>(PRODUCT_DICT.keySet());
        int perCat = TOTAL / catNames.size();

        for (int ci = 0; ci < catNames.size(); ci++) {
            String cat = catNames.get(ci);
            String[] products = PRODUCT_DICT.get(cat);
            String[] pool = IMAGE_POOL.get(cat);
            double[] rateRange = COMMISSION_RATES.get(cat);
            int limit = (ci < catNames.size() - 1) ? perCat : (TOTAL - idx);

            for (int pi = 0; pi < limit; pi++) {
                try {
                    String name = products[pi % products.length];
                    if (pi / products.length > 0) name += " " + (pi / products.length + 1);

                    BigDecimal price = getPriceForProduct(name, cat);
                    double minRate = rateRange[0], maxRate = rateRange[1];
                    BigDecimal rate = BigDecimal.valueOf(minRate + RANDOM.nextDouble() * (maxRate - minRate))
                            .setScale(4, RoundingMode.HALF_UP);

                    Product p = new Product();
                    p.setName(name);
                    p.setCategory(cat);
                    p.setPrice(price);
                    p.setStock(10 + RANDOM.nextInt(491));
                    p.setStatus(1);
                    p.setCommissionRate(rate);
                    p.setDescription(cat + "精选热销，" + name + "，正品保障全国包邮。");
                    p.setImageUrl(IMG_CDN + pool[pi % pool.length] + "/400/400");
                    batch.add(p);
                    idx++;

                    if (idx % 100 == 0) {
                        System.out.println("【流水线】已生成 " + idx + " / " + TOTAL + " 条");
                    }
                } catch (Exception e) {
                    System.err.println("【流水线】第 " + idx + " 条失败: " + e.getMessage());
                }
            }
        }

        if (!batch.isEmpty()) {
            saveBatch(batch, 100);
        }
        return String.format("流水线完成：成功 %d 条，失败 %d 条", idx, TOTAL - idx);
    }

    private BigDecimal getPriceForProduct(String name, String category) {
        // ── 母婴/奶粉: 260~420, 步长10 ──
        if (name.contains("奶粉")) {
            return BigDecimal.valueOf(260 + RANDOM.nextInt(17) * 10).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 母婴/纸尿裤: 89~169 ──
        if (name.contains("纸尿裤") || name.contains("拉拉裤")) {
            return BigDecimal.valueOf(89 + RANDOM.nextInt(81)).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 奶瓶/吸奶器: 50~300 ──
        if (name.contains("奶瓶") || name.contains("吸奶器") || name.contains("调奶器")
            || name.contains("消毒锅") || name.contains("安全座椅") || name.contains("婴儿推车")) {
            return BigDecimal.valueOf(50 + RANDOM.nextDouble() * 250).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 洗护/美妆(海飞丝/飘柔/清扬/潘婷等): 39~79 ──
        if (name.contains("海飞丝") || name.contains("飘柔") || name.contains("清扬")
            || name.contains("潘婷") || name.contains("舒肤佳") || name.contains("力士")) {
            return BigDecimal.valueOf(39 + RANDOM.nextInt(41)).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 美妆/个护类(非洗护): 89~599 ──
        if ("美妆/个护".equals(category)) {
            return BigDecimal.valueOf(89 + RANDOM.nextDouble() * 510).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 数码/手机: 4999~8999 ──
        if ("数码/手机".equals(category)) {
            return BigDecimal.valueOf(4999 + RANDOM.nextInt(4001)).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 电脑/办公: 2999~12999 ──
        if ("电脑/办公".equals(category)) {
            return BigDecimal.valueOf(2999 + RANDOM.nextDouble() * 10000).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 家电/电器: 299~4999 ──
        if ("家电/电器".equals(category)) {
            return BigDecimal.valueOf(299 + RANDOM.nextDouble() * 4700).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 食品/生鲜: 19~299 ──
        if ("食品/生鲜".equals(category)) {
            return BigDecimal.valueOf(19 + RANDOM.nextDouble() * 280).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 服饰/鞋包: 99~1999 ──
        if ("服饰/鞋包".equals(category)) {
            return BigDecimal.valueOf(99 + RANDOM.nextDouble() * 1900).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 运动/户外: 49~2999 ──
        if ("运动/户外".equals(category)) {
            return BigDecimal.valueOf(49 + RANDOM.nextDouble() * 2950).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 家居/日用: 9~499 ──
        if ("家居/日用".equals(category)) {
            return BigDecimal.valueOf(9 + RANDOM.nextDouble() * 490).setScale(2, RoundingMode.HALF_UP);
        }
        // ── 兜底: 39~199 ──
        return BigDecimal.valueOf(39 + RANDOM.nextDouble() * 160).setScale(2, RoundingMode.HALF_UP);
    }
}
