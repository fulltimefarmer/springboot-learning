SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for okta
-- ----------------------------
DROP TABLE IF EXISTS `okta`;
CREATE TABLE `okta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `client_id` varchar(80) COLLATE utf8_bin NOT NULL,
  `owner` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `status` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `secret` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `redirect_url` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `custom_scopes` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `application_type` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `grant_types` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_179m1trs2h292dn3ki88m0u1c` (`client_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
