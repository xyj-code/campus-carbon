SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_tree`;
CREATE TABLE `user_tree` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `tree_level` int NOT NULL DEFAULT 1,
  `tree_stage` varchar(32) NOT NULL DEFAULT 'seed',
  `tree_name` varchar(64) NOT NULL DEFAULT '微光种子',
  `growth_value` int NOT NULL DEFAULT 0,
  `water_value` int NOT NULL DEFAULT 0,
  `last_total_carbon` decimal(12,3) NOT NULL DEFAULT 0.000,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tree_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
