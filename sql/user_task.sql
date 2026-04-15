SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_task`;
CREATE TABLE `user_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `task_code` varchar(64) NOT NULL,
  `period_type` varchar(16) NOT NULL,
  `period_key` varchar(32) NOT NULL,
  `current_value` decimal(12,2) NOT NULL DEFAULT 0.00,
  `target_value` decimal(12,2) NOT NULL DEFAULT 0.00,
  `reward_points` int NOT NULL DEFAULT 0,
  `completed` tinyint NOT NULL DEFAULT 0,
  `reward_status` tinyint NOT NULL DEFAULT 0,
  `completed_time` datetime DEFAULT NULL,
  `reward_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_task_period` (`username`, `task_code`, `period_key`),
  KEY `idx_user_period` (`username`, `period_type`, `period_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
