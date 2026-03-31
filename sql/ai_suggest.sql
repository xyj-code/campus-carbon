/*
Navicat MySQL Data Transfer

Source Server         : 实验
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : campus_carbon

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-03-31 15:18:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ai_suggest
-- ----------------------------
DROP TABLE IF EXISTS `ai_suggest`;
CREATE TABLE `ai_suggest` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '关联学生ID',
  `input_text` text NOT NULL COMMENT '用户输入的碳足迹',
  `suggest_text` text NOT NULL COMMENT '百炼AI返回的建议',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `ai_suggest_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI建议表';
