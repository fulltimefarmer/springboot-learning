USE auth;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for okta
-- ----------------------------
DROP TABLE IF EXISTS `okta`;
CREATE TABLE `okta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `application_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `client_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `custom_scopes` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `grant_types` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `redirect_url` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `secret` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `describe` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK_179m1trs2h292dn3ki88m0u1c` (`client_id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
