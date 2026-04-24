SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `agent_memory`;
CREATE TABLE `agent_memory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `last_session_id` varchar(64) DEFAULT NULL,
  `last_user_note` varchar(255) DEFAULT NULL,
  `verified_completion_count` int NOT NULL DEFAULT 0,
  `claimed_completion_count` int NOT NULL DEFAULT 0,
  `skipped_count` int NOT NULL DEFAULT 0,
  `last_active_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_memory_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
