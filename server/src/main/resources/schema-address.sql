-- ===================================================
-- 收货地址表 DDL
-- ===================================================
CREATE TABLE IF NOT EXISTS `address` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  `user_id`     INT           NOT NULL                 COMMENT '用户ID(users.user_id)',
  `name`        VARCHAR(32)   NOT NULL                 COMMENT '收货人姓名',
  `phone`       VARCHAR(16)   NOT NULL                 COMMENT '收货人手机号',
  `province`    VARCHAR(32)   NOT NULL DEFAULT ''       COMMENT '省份',
  `city`        VARCHAR(32)   NOT NULL DEFAULT ''       COMMENT '城市',
  `district`    VARCHAR(32)   NOT NULL DEFAULT ''       COMMENT '区/县',
  `detail`      VARCHAR(255)  NOT NULL DEFAULT ''       COMMENT '详细地址',
  `is_default`  TINYINT       NOT NULL DEFAULT 0        COMMENT '是否默认地址: 1是 0否',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';
