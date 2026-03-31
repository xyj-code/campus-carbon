/*
Navicat MySQL Data Transfer

Source Server         : 实验
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : campus_carbon

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-03-31 15:17:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for step_count
-- ----------------------------
DROP TABLE IF EXISTS `step_count`;
CREATE TABLE `step_count` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '关联学生ID',
  `date` date NOT NULL COMMENT '统计日期',
  `step_num` int NOT NULL COMMENT '步数',
  `calorie` double NOT NULL COMMENT '卡路里',
  `duration` int NOT NULL COMMENT '运动时长(分钟)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date` (`student_id`,`date`),
  CONSTRAINT `step_count_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='步数统计表';
