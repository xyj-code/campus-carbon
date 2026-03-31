/*
Navicat MySQL Data Transfer

Source Server         : 实验
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : campus_carbon

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-03-31 15:17:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sport_record
-- ----------------------------
DROP TABLE IF EXISTS `sport_record`;
CREATE TABLE `sport_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '关联学生ID',
  `sport_type` varchar(50) NOT NULL COMMENT '运动类型(走路/跑步/骑行)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `distance` double NOT NULL COMMENT '距离',
  `calorie` double NOT NULL COMMENT '消耗',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `sport_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='运动记录表';
