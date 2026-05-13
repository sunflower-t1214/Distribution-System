<p align="center">
  <img src="https://img.icons8.com/fluency/96/shop.png" alt="logo" width="80" />
</p>

<h1 align="center">Distribution-System</h1>
<p align="center">
  <strong>全场景智能分销管理平台 · Full-Stack Smart Distribution Platform</strong>
  <br />
  S2B2C 模式 · 验证码激活身份 · 自动化佣金结算
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-17-blue" alt="Java" />
  <img src="https://img.shields.io/badge/UniApp-Vue%203-4fc08d" alt="UniApp" />
  <img src="https://img.shields.io/badge/MySQL-8.0-orange" alt="MySQL" />
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.5-blueviolet" alt="MyBatis-Plus" />
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License" />
</p>

---

## 📋 项目简介 · Introduction

**Distribution-System** 是一个基于 **S2B2C** 模式的通用商品分销管理平台。系统包含消费者端、分销员端和管理员端三个入口，适用于电商、食品、美妆等多个行业的商品分销场景，仅需更换商品数据即可快速复用。

> **Distribution-System** is a full-stack distribution management platform built on the **S2B2C** model. It provides three separate portals for consumers, distributors, and administrators, making it suitable for e-commerce, food, beauty, and other retail industries. Simply swap product data for instant reuse.

### 核心亮点 · Highlights

| 特性 | 说明 |
|------|------|
| 🔐 **动态验证码身份激活** | 分销员通过短信验证码激活身份，无需管理员手动开通 |
| 💰 **自动化分佣** | 订单完成后自动计算佣金，支持多级分成（开发中） |
| 👥 **三端统一架构** | 消费者、销售员、管理员三端在同一个 UniApp 中通过角色切换 |
| 🚀 **阿里云 OSS 托管** | 即将支持商品图片自动上传至阿里云 OSS（开发中） |
| 📊 **数据仪表盘** | 销售员和管理员实时查看业绩和系统统计数据 |

---

## 🧱 技术选型 · Tech Stack

### 后端 · Backend

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 核心开发语言 |
| **Spring Boot** | 3.3.4 | 微服务框架 |
| **MyBatis-Plus** | 3.5.5 | ORM 持久层框架 |
| **MySQL** | 8.0 | 关系型数据库 |
| **Maven** | 3.9+ | 项目构建 |

### 前端 · Frontend

| 技术 | 版本 | 用途 |
|------|------|------|
| **UniApp** | — | 跨端应用框架 |
| **Vue 3** | 3.3.4 | 响应式 UI 框架 |
| **Vite** | — | 前端构建工具 |

### 架构图 · Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    UniApp (Vue 3)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐      │
│  │ Consumer  │  │  Sales   │  │     Admin        │      │
│  │   端      │  │   端     │  │      端          │      │
│  └────┬─────┘  └────┬─────┘  └───────┬──────────┘      │
│       └──────────────┴────────────────┘                 │
│                         │ HTTP/JSON                     │
├─────────────────────────┼───────────────────────────────┤
│              Spring Boot 3 (8080)                        │
│  ┌──────────┐ ┌───────┐ ┌──────────┐ ┌───────────┐     │
│  │ Controller││Service││ MyBatis+  │ │   Auth    │      │
│  └──────────┘ └───────┘ └──────────┘ └───────────┘      │
├─────────────────────────┼───────────────────────────────┤
│                     MySQL 数据库                         │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌─────────┐      │
│  │users │ │sales │ │orders│ │products│ │order_item│     │
│  └──────┘ └──────┘ └──────┘ └──────┘ └─────────┘      │
└─────────────────────────────────────────────────────────┘
```

---

## ✨ 功能清单 · Features

### 🛒 消费者端 · Consumer Portal

| 功能 | 描述 |
|------|------|
| 首页 | 搜索栏 + 轮播图 + 带"分享赚"标签的商品卡片 |
| 商品列表 | 两列布局，分类筛选，价格/销量排序 |
| 商品详情 | 图片轮播 + 商品信息 + 加购/购买按钮 |
| 购物车 | 商品管理 + 全选/结算 |
| 订单中心 | 全部/待付款/待发货/已完成 标签切换 |
| 个人中心 | 编辑资料、订单查询、角色切换 |

### 📢 分销员端 · Sales Portal

| 功能 | 描述 |
|------|------|
| 验证码激活 | 动态 6 位验证码激活身份（5 分钟有效） |
| 销售仪表盘 | 今日销售额、本月佣金、推广订单数 |
| 推广商品 | 商品列表 + 生成推广链接 + 分享 |
| 佣金中心 | 余额查看 + 累计/已提现/待结算统计 |
| 客户管理 | 客户列表 + 联系方式 |
| 推广工具 | 推广二维码、分享海报、商品链接 |

### ⚙️ 管理员端 · Admin Portal

| 功能 | 描述 |
|------|------|
| 管理看板 | 今日销售额、总订单数、用户/销售员统计 |
| 用户管理 | 用户列表 + 封禁账号 |
| 销售员管理 | 销售员列表 + 创建/编辑 |
| 商品管理 | 商品 CRUD + 上架/下架 |
| 订单管理 | 全部订单 + 修改状态 |
| 佣金管理 | 佣金审核 + 数据导出（开发中）|
| 数据统计 | 销售额、商品排名、销售员排名、用户增长 |

---

## 🚀 快速启动 · Quick Start

### 前置条件 · Prerequisites

- **JDK 17** 或更高版本
- **MySQL 8.0+** 
- **Maven 3.9+**（或使用项目内 Maven Wrapper）
- **HBuilderX**（用于运行 UniApp 前端）
- **Node.js 18+**（可选，用于 CLI 模式运行前端）

### 第一步：克隆项目 · Clone

```bash
git clone https://github.com/your-username/Distribution-System.git
cd Distribution-System
```

### 第二步：初始化数据库 · Database

在 MySQL 中创建数据库并执行建表脚本：

```sql
CREATE DATABASE distribution_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

建表 DDL 请参考项目根目录下的 `docs/ddl.sql`（需自行准备，或根据实体类自动建表）。

### 第三步：启动后端 · Start Backend

```bash
cd server

# 配置数据库连接（编辑 application.yaml）
# 修改 spring.datasource.url / username / password

# 编译并启动
.\mvnw.cmd clean spring-boot:run
```

后端将在 `http://localhost:8080` 启动。验证：

```bash
curl http://localhost:8080/api/products/list
```

### 第四步：启动前端 · Start Frontend

#### 方式 A：HBuilderX（推荐）

1. 打开 HBuilderX
2. 文件 → 导入 → 选择 `user/user-app` 目录
3. 运行 → 运行到浏览器

#### 方式 B：CLI 模式

```bash
cd user/user-app
npm install
npm run dev:h5
```

前端将在 `http://localhost:5173` 启动（需项目根目录已有 `package.json`）。

---

## 🔑 身份体系 · Role System

系统支持三种身份，通过前端缓存和后端验证码双重校验：

| 身份 | 标识 | 权限 |
|------|------|------|
| 消费者 | `USER` | 浏览/购买商品，查看订单 |
| 分销员 | `SALES` | 推广商品，查看佣金和客户 |
| 管理员 | `ADMIN` | 全部管理权限 |

**激活流程**：用户在个人中心点击切换 → 获取验证码 → 后端生成 6 位码并打印到控制台 → 输入验证码 → 身份切换成功。

---

## 📁 项目结构 · Project Structure

```
Distribution-System/
├── server/                          # Spring Boot 后端
│   ├── src/main/java/com/example/server/
│   │   ├── common/                  # 通用工具（Result 统一返回）
│   │   ├── config/                  # 配置（CORS、Web）
│   │   ├── controller/              # 控制器层
│   │   │   ├── AuthController.java  # 验证码生成与校验
│   │   │   ├── UserController.java  # 用户注册/登录
│   │   │   ├── ProductController.js # 商品 API
│   │   │   ├── OrderController.java # 订单 API
│   │   │   ├── CartController.java  # 购物车 API
│   │   │   └── SalesController.java # 分销 API
│   │   ├── entity/                  # 实体类
│   │   ├── mapper/                  # MyBatis-Plus 映射
│   │   └── service/                 # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yaml         # 数据库等配置
│   └── pom.xml
│
└── user/
    └── user-app/                    # UniApp 前端
        ├── pages/
        │   ├── home/                # 消费者首页
        │   ├── order/               # 订单页面
        │   ├── cart/                # 购物车
        │   ├── sales/               # 销售端（6 个页面）
        │   ├── admin/               # 管理端（7 个页面）
        │   └── userInfo/            # 个人中心（角色切换）
        ├── utils/
        │   └── request.js           # HTTP 请求工具
        ├── pages.json               # 路由配置
        └── manifest.json            # 应用配置
```

---

## 🧪 测试数据 · Test Data

```sql
-- 插入测试商品
INSERT INTO products (name, price, stock, status, image_url, commission_rate)
VALUES 
('HUAWEI Mate 80 Pro', 8499, 100, 1, 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg', 5.00),
('MacBook Air M3', 8999, 50, 1, 'https://images.pexels.com/photos/2047905/pexels-photo-2047905.jpeg', 3.00),
('AirPods Pro 2', 1899, 200, 1, 'https://images.pexels.com/photos/3394651/pexels-photo-3394651.jpeg', 8.00);

-- 插入测试用户（密码明文，生产环境请加密）
INSERT INTO users (name, phone, password, register_time)
VALUES ('张三', '13800138001', '123456', NOW());
```

---

## 🔮 路线图 · Roadmap

- [x] 消费者购物全流程（首页 → 详情 → 购物车 → 订单）
- [x] 分销员验证码身份激活
- [x] 管理员后台管理
- [ ] 阿里云 OSS 图片自动托管
- [ ] 分销多级分成
- [ ] 微信小程序打包发布
- [ ] 分销海报自动生成
- [ ] 佣金提现（微信/支付宝）

---

## 📄 许可证 · License

[MIT](LICENSE)

---

<p align="center">
  Made with ❤️ by Team Distribution-System
</p>
