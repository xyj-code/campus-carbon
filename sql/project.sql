/*
Navicat MySQL Data Transfer

Source Server         : 实验
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : campus_carbon

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-03-31 15:17:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '项目名称',
  `type` varchar(50) NOT NULL COMMENT '项目类型：tree-梭梭树, porpoise-江豚, library-图书室',
  `location` varchar(255) NOT NULL COMMENT '项目所在地',
  `description` text COMMENT '项目介绍',
  `progress` int DEFAULT '0' COMMENT '项目进度百分比',
  `images` text COMMENT '项目实拍图URL，多个用逗号分隔',
  `required_carbon` decimal(10,2) NOT NULL COMMENT '解锁所需减碳量kg',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-进行中，0-已结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公益项目表';
