SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gcsc_group
-- ----------------------------
DROP TABLE IF EXISTS `gcsc_group`;
CREATE TABLE `gcsc_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8_bin NOT NULL,
  `remark` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_eo1w6hnqd2q7o8ao851s8t13f` (`name`),
  KEY `FKey887hj9ettn3dolg1vn5b8gd` (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


SET FOREIGN_KEY_CHECKS = 1;
