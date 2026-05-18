<p align="center">
  <img src="https://img.icons8.com/fluency/96/shop.png" alt="logo" width="80" />
</p>

<h1 align="center">Distribution-System</h1>
<p align="center">
  <strong>高并发安全分布式分销商城 · High-Concurrency Secure Distribution Platform</strong>
  <br />
  Spring Boot 3 · Redis · Redisson · UniApp (Vue 3) · JWT · BCrypt
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Redis-8.6.3-red" alt="Redis" />
  <img src="https://img.shields.io/badge/Redisson-3.30.0-orange" alt="Redisson" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL" />
  <img src="https://img.shields.io/badge/UniApp-Vue%203-4fc08d" alt="UniApp" />
  <img src="https://img.shields.io/badge/JDK-17%2F25-lightgrey" alt="JDK" />
</p>

---

## 📋 项目简介

基于 **Spring Boot 3 + Redis + Redisson + UniApp (Vue 3)** 的全栈分销商城系统。覆盖消费者购物、分销员裂变推广、管理员后台管控三大端，已具备生产级高并发防护与金融级佣金安全能力。

### 核心架构理念

```
┌─────────────────────────────────────────────────────────────────┐
│                    UniApp (Vue 3) 前端                           │
│  消费者端 · 分销员端 · 管理员端 · 邀请有奖 · 团队管理           │
├─────────────────────────────────────────────────────────────────┤
│                 Spring Boot 3 后端 API                           │
│  JWT 鉴权 → BCrypt 加密 → 分布式锁 → 异步队列 → 佣金流水      │
├──────────────────────┬──────────────────────────────────────────┤
│   Redis 8.6.3        │         MySQL 8.0                        │
│   热点库存 · 缓存    │        订单 · 商品 · 用户 · 流水        │
│   阻塞队列 · 分布式锁│                                          │
└──────────────────────┴──────────────────────────────────────────┘
```

---

## 🧱 核心技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 3.3.4 | 微服务框架 |
| **JDK** | 17 / 25 | 运行环境 |
| **MyBatis-Plus** | 3.5.5 | ORM 持久层 |
| **MySQL** | 8.0 | 关系型数据库 |
| **Redis** | 8.6.3 | 缓存 + 分布式锁 + 消息队列 |
| **Redisson** | 3.30.0 | 分布式锁、阻塞队列 |
| **JJWT** | 0.12.3 | JWT Token 签发与验证 |
| **Spring Security Crypto** | — | BCrypt 密码加密 |
| **Maven** | 3.9+ | 构建工具 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| **UniApp** | — | 跨端应用框架 |
| **Vue 3** | 3.3.4 | 响应式 UI |
| **Vite** | — | 构建工具 |
| **ES6+** | — | 开发语言 |

---

## ✨ 四大商业级核心亮点

### 🔒 1. 高并发防超卖系统

```
下单请求
    ↓
Redisson 分布式锁（粒度：每个 productId 独立一把锁）
    ↓ 锁按字典序排列获取（防止 AB/BA 死锁）
Redis 热点库存原子扣减（decrement）
    ↓
MySQL 最终库存写入
    ↓
锁释放
```

- **不是锁表**，是锁单个商品 ID，并发量提升 N 倍
- **不是 JVM 锁**，是 Redis 跨进程锁，多实例部署也安全
- **锁超时兜底**：3 秒获取锁超时 → 返回"系统繁忙"

### 💰 2. 金融级佣金防刷（强幂等）

```
支付成功 → 发消息到 Redis 阻塞队列
    ↓
后台守护线程消费
    ↓
查询 commission_log 是否已有该 order_id 的记录？
    ├─ 已存在 → 直接跳过（防重闸）
    └─ 不存在 → 计算佣金 → 增余额 → INSERT 流水（状态=已结算）
```

- **防重复计算**：`commission_log` 里 `order_id` 一旦写入，永不重复加钱
- **每一分钱有据可查**：流水表记录订单金额、佣金率、佣金金额、结算时间
- **支付与算钱解耦**：用户秒级收到成功反馈，佣金后台异步慢慢算

### ⚡ 3. 异步消峰解耦

```
改造前（同步阻塞）：
  支付 → 改订单 → 查用户 → 算佣金 → 增余额 → 写流水 → 返回（全部同步）

改造后（异步解耦）：
  支付 → 改订单 → queue.offer(orderId) → 立即返回（毫秒级）
                            ↓
              queue.take() → 异步算钱（后台守护线程）
```

- 支付接口响应时间从 **几百毫秒降到个位数毫秒**
- Redis 阻塞队列 `take()` 无消息时不占 CPU
- 宕机重启消息不丢（Redis RDB/AOF 持久化）

### 🛡️ 4. 全线安全防御

| 防线 | 实现 |
|---|---|
| **密码安全** | BCrypt 单向哈希，旧用户登录时无感自动迁移加密 |
| **身份认证** | JWT Token，7 天有效期，每次请求拦截器校验 |
| **防重复点击** | 前端全局 Loading 遮罩 + 计数器，请求期间页面不可操作 |
| **防 SQL 注入** | MyBatis-Plus 参数预编译，天然免疫 |
| **CORS** | 配置跨域白名单 |
| **佣金防刷** | commission_log 幂等校验 |

---

## 🗄️ 数据库核心表

| 表名 | 核心字段 | 用途 |
|------|---------|------|
| `users` | `user_id, phone, password(BCrypt), inviter_id, commission_balance, role` | 用户 + 分销 + 余额 |
| `products` | `product_id, name, price, stock, commission_rate, status` | 商品 + 佣金率 |
| `order_info` | `order_id, order_sn, user_id, total_amount, status(0~4), receiver_*` | 订单 + 状态机 |
| `order_item` | `id, order_id, product_id, product_price(快照), quantity` | 订单商品快照 |
| `commission_log` | `id, order_id(唯一), distributor_id, commission_amount, status(0/1/2)` | 佣金流水（防重） |
| `cart` | `id, user_id, product_id, quantity` | 购物车 |
| `address` | `id, user_id, name, phone, province/city/district/detail, is_default` | 收货地址 |
| `invite_record` | `id, user_id, inviter_id` | 邀请关系记录 |
| `withdraw_apply` | `id, distributor_id, amount, status` | 提现申请 |

---

## 🚀 傻瓜式本地运行与全链路自测指南

### 前置条件

| 软件 | 版本要求 | 检查命令 |
|------|---------|---------|
| JDK | 17+ | `java -version` |
| MySQL | 8.0+ | `mysql --version` |
| Redis | 6.0+ | `redis-cli ping` |
| Maven | 3.9+ | `mvn --version` |

### 第一步：启动 Redis

```bash
# Windows 下启动 Redis（已在 D:\Redis 目录下）
D:\Redis\redis-server.exe
```

保持此窗口不关闭，另开新窗口执行后续步骤。

### 第二步：初始化数据库

在 MySQL 中执行以下 SQL 文件（按顺序）：

```bash
# 在 MySQL 命令行或 Navicat 中执行
source D:\Distribution_System\server\src\main\resources\schema-distribution.sql
source D:\Distribution_System\server\src\main\resources\schema-distribution-v2.sql
source D:\Distribution_System\server\src\main\resources\schema-address.sql
```

### 第三步：启动后端

```bash
cd D:\Distribution_System\server
.\mvnw.cmd clean spring-boot:run
```

看到 `Started ServerApplication in 2.4 seconds` 即成功。

### 第四步：启动前端

用 **HBuilderX** 打开 `D:\Distribution_System\user\user-app` → 运行 → 运行到浏览器。

---

### 🧪 全链路自测（PowerShell）

以下测试请在**新开**的 PowerShell 窗口中执行（不要关后端和 Redis）。

#### 测试 1：新用户注册（BCrypt 加密）

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/user/register" -Method Post -ContentType "application/json" -Body '{"name":"张三","phone":"15000000001","password":"mypwd123"}'
```

✅ 注册完成后，去 MySQL 检查 `users` 表，`password` 字段应为一串 `$2a$10$...` 密文，不是明文 `mypwd123`。

#### 测试 2：旧用户无感迁移加密

```powershell
# 如果你之前有明文密码的老账号（如 15800000001/123），直接登录
Invoke-RestMethod -Uri "http://localhost:8080/user/login" -Method Post -ContentType "application/json" -Body '{"phone":"15800000001","password":"123"}'
```

✅ 登录成功后，去 MySQL 检查，该用户的 `password` 应自动变为 `$2a$10$...` 密文。

#### 测试 3：邀请 + 下单 + 异步佣金全链路

```powershell
# 3.1 注册上级 A
$regA = Invoke-RestMethod -Uri "http://localhost:8080/user/register" -Method Post -ContentType "application/json" -Body '{"name":"上级A","phone":"15100000001","password":"123"}'
$inviteCode = $regA.data.user.userId + 10000
Write-Host "A的邀请码: $inviteCode"

# 3.2 注册下级 B（填A的邀请码）
$bodyB = '{"name":"下级B","phone":"15100000002","password":"123","inviteCode":' + $inviteCode + '}'
Invoke-RestMethod -Uri "http://localhost:8080/user/register" -Method Post -ContentType "application/json" -Body $bodyB

# 3.3 B登录
$loginB = Invoke-RestMethod -Uri "http://localhost:8080/user/login" -Method Post -ContentType "application/json" -Body '{"phone":"15100000002","password":"123"}'
$tokenB = $loginB.data.token

# 3.4 B下单
$orderRes = Invoke-RestMethod -Uri "http://localhost:8080/api/order/create" -Method Post -ContentType "application/json" -Headers @{"Authorization"="Bearer $tokenB"} -Body '{"items":[{"productId":1,"quantity":1}]}'
Write-Host "订单ID: $($orderRes.orderId)"
```

**震撼效果验证**：

```powershell
# 3.5 B支付（异步！瞬间返回！）
Invoke-RestMethod -Uri "http://localhost:8080/api/order/pay" -Method Post -ContentType "application/json" -Headers @{"Authorization"="Bearer $tokenB"} -Body "{\"id\":$($orderRes.orderId)}"
```

预期结果：
1. **前台秒回**：`{"code":200,"msg":"支付成功"}` — 不等待算钱
2. **后台消费**：看 IDEA 控制台，1-2 秒后出现：
   ```
   【异步佣金】订单 26 佣金已结算
   ```
3. **数据库对账**：执行以下 SQL：

```sql
-- 查看佣金流水
SELECT * FROM commission_log WHERE order_id = $($orderRes.orderId);

-- 查看上级余额增加了
SELECT user_id, name, commission_balance FROM users WHERE phone = '15100000001';
```

---

## 📁 项目结构

```
Distribution-System/
├── server/                          # Spring Boot 后端
│   ├── src/main/java/com/example/server/
│   │   ├── common/                  # JwtUtils, UserContext(ThreadLocal), Result
│   │   ├── config/                  # WebConfig, LoginInterceptor, SecurityConfig
│   │   │   └── CommissionConsumer.java  # 异步佣金消费守护线程
│   │   ├── controller/              # OrderController, ProductController,
│   │   │                              UserController, AddressController,
│   │   │                              SalesController, AuthController,
│   │   │                              DistributorBindController
│   │   ├── entity/                  # Order, Product, User, Cart, Address,
│   │   │                              OrderItem, Sales, CommissionLog
│   │   ├── mapper/                  # MyBatis-Plus Mapper 接口
│   │   └── service/                 # 业务逻辑 + OrderServiceImpl(佣金计算)
│   ├── src/main/resources/
│   │   ├── application.yaml         # 数据库/Redis/OSS 配置
│   │   └── schema-*.sql             # 建表 DDL
│   └── pom.xml                      # 依赖管理（含 Redisson, JJWT, BCrypt）
│
└── user/
    └── user-app/                    # UniApp 前端
        ├── pages/
        │   ├── home/                # 消费者首页
        │   ├── order/               # 订单列表 + 详情
        │   ├── cart/                # 购物车
        │   ├── sales/               # 分销员视图
        │   ├── share/               # 邀请有奖
        │   ├── team/                # 团队三级页面
        │   ├── settings/            # 账号设置
        │   └── userInfo/            # 个人中心
        └── utils/
            └── request.js           # HTTP 请求封装（全局 Loading 遮罩）
```

---

## 📄 许可证

[MIT](LICENSE)

<p align="center">
  Built with ❤️ by Team Distribution-System
</p>
