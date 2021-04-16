USE auth;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gcsc_project
-- ----------------------------
DROP TABLE IF EXISTS `gcsc_project`;
CREATE TABLE `gcsc_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) COLLATE utf8_bin NOT NULL,
  `description` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_bnisvw1ne3gn078f4qwuo597k` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of gcsc_project
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
