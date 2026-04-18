SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `activity_topic`;
CREATE TABLE `activity_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_code` varchar(64) NOT NULL,
  `title` varchar(128) NOT NULL,
  `subtitle` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(512) NOT NULL DEFAULT '',
  `banner_title` varchar(128) NOT NULL DEFAULT '',
  `banner_subtitle` varchar(255) NOT NULL DEFAULT '',
  `cover_emoji` varchar(16) NOT NULL DEFAULT 'LEAF',
  `theme_color` varchar(32) NOT NULL DEFAULT '#3f9a63',
  `reward_text` varchar(255) NOT NULL DEFAULT '',
  `target_text` varchar(255) NOT NULL DEFAULT '',
  `task_codes` varchar(255) NOT NULL DEFAULT '',
  `status` tinyint NOT NULL DEFAULT 1,
  `featured` tinyint NOT NULL DEFAULT 0,
  `sort_order` int NOT NULL DEFAULT 0,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_code` (`activity_code`),
  KEY `idx_activity_status` (`status`, `featured`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `activity_topic`
(`activity_code`, `title`, `subtitle`, `description`, `banner_title`, `banner_subtitle`, `cover_emoji`, `theme_color`, `reward_text`, `target_text`, `status`, `featured`, `sort_order`, `start_time`, `end_time`)
VALUES
('EARTH_DAY_2026', '地球日低碳挑战周', '围绕绿色出行、低碳运动与权益兑换打造校园专题活动。', '活动期间完成每日任务与每周挑战，可自动获得积分奖励，并联动绿色权益、徽章与证书，形成完整激励闭环。', '一起加入地球日低碳挑战周', '完成任务后自动结算积分，并解锁绿色权益与成就展示。', 'EARTH', '#3f9a63', '任务奖励 + 绿色权益 + 徽章证书', '聚焦步数、运动与权益兑换三类核心行为', 1, 1, 10, '2026-04-01 00:00:00', '2026-05-10 23:59:59'),
('GREEN_COMMUTE_MONTH', '校园绿色出行月', '通过步数与运动记录鼓励学生养成持续低碳习惯。', '活动强调每日步行目标与每周累计挑战，以校园运营视角推动低碳行为长期留存。', '用一整个月点亮绿色出行', '坚持记录步数与运动，平台会自动累计任务进度。', 'BIKE', '#4fa8bb', '周挑战奖励 + 排行榜曝光', '主打累计步数和运动次数挑战', 1, 0, 20, '2026-04-08 00:00:00', '2026-05-31 23:59:59');

SET FOREIGN_KEY_CHECKS = 1;
