SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `agent_session`;
CREATE TABLE `agent_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(64) NOT NULL,
  `username` varchar(64) NOT NULL,
  `session_status` varchar(32) NOT NULL DEFAULT 'ready',
  `current_action_id` varchar(64) DEFAULT NULL,
  `user_note` varchar(255) DEFAULT NULL,
  `plan_version` int NOT NULL DEFAULT 1,
  `last_active_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_session_id` (`session_id`),
  KEY `idx_agent_username_time` (`username`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
