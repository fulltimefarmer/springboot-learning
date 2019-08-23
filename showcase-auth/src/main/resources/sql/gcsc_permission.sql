/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : auth

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 23/07/2019 11:10:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gcsc_permission
-- ----------------------------
DROP TABLE IF EXISTS `gcsc_permission`;
CREATE TABLE `gcsc_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8_bin NOT NULL,
  `type` int(4) NOT NULL,
  `method` varchar(40) COLLATE utf8_bin NOT NULL,
  `uri_reg_pattern` varchar(255) COLLATE utf8_bin NOT NULL,
  `remark` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dbhgcdjeen1torvp09macit7r` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
