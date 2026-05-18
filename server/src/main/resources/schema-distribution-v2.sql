-- ===================================================
-- 分销核心逻辑 DDL：邀请关系 + 佣金余额
-- ===================================================

ALTER TABLE users
  ADD COLUMN inviter_id INT DEFAULT NULL COMMENT '邀请人ID(users.user_id)',
  ADD COLUMN commission_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '佣金余额',
  ADD INDEX idx_inviter (inviter_id);

CREATE TABLE IF NOT EXISTS `invite_record` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT,
  `user_id`     INT           NOT NULL COMMENT '被邀请人ID',
  `inviter_id`  INT           NOT NULL COMMENT '邀请人ID',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user` (`user_id`),
  KEY `idx_inviter` (`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请关系记录表';
