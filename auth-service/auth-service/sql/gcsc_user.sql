USE auth;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gcsc_user
-- ----------------------------
DROP TABLE IF EXISTS `gcsc_user`;
CREATE TABLE `gcsc_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(2) NOT NULL COMMENT '1:nike user; 2:external user;',
  `username` varchar(80) COLLATE utf8_bin NOT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `display_name` varchar(80) COLLATE utf8_bin NOT NULL,
  `remark` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `create_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  `last_modify_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7qx9hpubgedxwh5es4pam2ij5` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
