SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `agent_action_log`;
CREATE TABLE `agent_action_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(64) NOT NULL,
  `action_id` varchar(64) NOT NULL,
  `task_code` varchar(64) DEFAULT NULL,
  `step_order` int NOT NULL DEFAULT 0,
  `title` varchar(128) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `priority_tag` varchar(64) DEFAULT NULL,
  `duration_minutes` int DEFAULT NULL,
  `estimated_carbon_saving` int DEFAULT NULL,
  `estimated_points` int DEFAULT NULL,
  `action_text` varchar(64) DEFAULT NULL,
  `action_path` varchar(255) DEFAULT NULL,
  `action_type` varchar(32) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'pending',
  `result_note` varchar(255) DEFAULT NULL,
  `completion_mode` varchar(32) DEFAULT NULL,
  `verification_status` varchar(32) DEFAULT NULL,
  `execution_hint` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_action` (`session_id`, `action_id`),
  KEY `idx_agent_action_session_order` (`session_id`, `step_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
