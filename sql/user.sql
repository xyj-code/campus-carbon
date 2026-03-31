/*
Navicat MySQL Data Transfer

Source Server         : 实验
Source Server Version : 80028
Source Host           : localhost:3306
Source Database       : campus_carbon

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2026-03-31 15:16:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小程序唯一标识',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `password` varchar(100) NOT NULL DEFAULT '123456',
  `points` int NOT NULL DEFAULT '0' COMMENT '当前积分',
  PRIMARY KEY (`id`,`username`),
  UNIQUE KEY `stu_no` (`username`),
  UNIQUE KEY `openid` (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
