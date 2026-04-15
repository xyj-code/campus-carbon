SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `task_template`;
CREATE TABLE `task_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_code` varchar(64) NOT NULL,
  `period_type` varchar(16) NOT NULL,
  `metric_type` varchar(32) NOT NULL,
  `target_value` decimal(12,2) NOT NULL,
  `reward_points` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `sort_order` int NOT NULL DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_code` (`task_code`),
  KEY `idx_task_period` (`period_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `task_template`
(`task_code`, `period_type`, `metric_type`, `target_value`, `reward_points`, `status`, `sort_order`)
VALUES
('DAILY_STEP_6000', 'DAILY', 'STEP_TOTAL', 6000.00, 30, 1, 10),
('DAILY_SPORT_2KM', 'DAILY', 'SPORT_DISTANCE_TOTAL', 2.00, 20, 1, 20),
('DAILY_CHECKIN_1', 'DAILY', 'ACTIVE_DAY_COUNT', 1.00, 10, 1, 30),
('WEEKLY_STEP_50000', 'WEEKLY', 'STEP_TOTAL', 50000.00, 80, 1, 40),
('WEEKLY_SPORT_3_TIMES', 'WEEKLY', 'SPORT_RECORD_COUNT', 3.00, 60, 1, 50),
('WEEKLY_REDEEM_1', 'WEEKLY', 'BENEFIT_REDEEM_COUNT', 1.00, 40, 1, 60);

SET FOREIGN_KEY_CHECKS = 1;
