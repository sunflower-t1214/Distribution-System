-- ===================================================
-- 分销系统核心表 DDL
-- 主配色：淘宝红 #E4393C | 极简白 #FFFFFF | 浅灰 #F4F4F4
-- ===================================================

-- 1. 商品表补充字段（首次执行即可，重复执行会报错但无影响）
-- ALTER TABLE products ADD COLUMN dist_category VARCHAR(32) DEFAULT NULL COMMENT '分销分类标签';

-- 2. 分销员表
CREATE TABLE IF NOT EXISTS `distribution_user` (
  `id`          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`     INT             NOT NULL                 COMMENT '关联用户ID(users.user_id)',
  `parent_id`   INT             DEFAULT NULL             COMMENT '上级分销员ID(分销链)',
  `level`       TINYINT         NOT NULL DEFAULT 1       COMMENT '分销等级:1普通 2高级 3合伙人',
  `total_commission`    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计佣金',
  `withdrawable_commission` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '可提现佣金',
  `total_sales` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计销售额',
  `order_count` INT NOT NULL DEFAULT 0 COMMENT '推广订单数',
  `status`      TINYINT         NOT NULL DEFAULT 1       COMMENT '状态:1正常 0冻结',
  `apply_time`  DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分销员表';

-- 3. 佣金流水表
CREATE TABLE IF NOT EXISTS `commission_log` (
  `id`                BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id`          BIGINT          NOT NULL                COMMENT '关联订单ID(order_info.order_id)',
  `distributor_id`    INT             NOT NULL                COMMENT '分销员ID',
  `order_amount`      DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '订单金额',
  `commission_rate`   DECIMAL(5,2)    NOT NULL DEFAULT 0.00   COMMENT '佣金比例(%)',
  `commission_amount` DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '佣金金额',
  `status`            TINYINT         NOT NULL DEFAULT 0       COMMENT '状态:0待结算 1已结算 2已失效',
  `settle_time`       DATETIME        DEFAULT NULL             COMMENT '结算时间',
  `remark`            VARCHAR(255)    DEFAULT NULL             COMMENT '备注',
  `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_distributor` (`distributor_id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='佣金流水表';

-- 4. 提现申请表
CREATE TABLE IF NOT EXISTS `withdraw_apply` (
  `id`                BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `distributor_id`    INT             NOT NULL                COMMENT '分销员ID',
  `amount`            DECIMAL(10,2)   NOT NULL                COMMENT '提现金额',
  `status`            TINYINT         NOT NULL DEFAULT 0       COMMENT '状态:0待审核 1已打款 2已拒绝',
  `account_type`      VARCHAR(16)     DEFAULT 'wechat'        COMMENT '提现方式:wechat/alipay/bank',
  `account_info`      VARCHAR(128)    DEFAULT NULL             COMMENT '提现账号',
  `audit_time`        DATETIME        DEFAULT NULL             COMMENT '审核时间',
  `remark`            VARCHAR(255)    DEFAULT NULL             COMMENT '审核备注',
  `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_distributor` (`distributor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现申请表';
