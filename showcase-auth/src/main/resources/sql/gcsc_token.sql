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

 Date: 16/08/2019 16:45:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gcsc_token
-- ----------------------------
DROP TABLE IF EXISTS `gcsc_token`;
CREATE TABLE `gcsc_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(400) COLLATE utf8_bin NOT NULL,
  `username` varchar(80) COLLATE utf8_bin NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `create_time` bigint(20) NOT NULL,
  `expire_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
