SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`username` varchar(255) COLLATE utf8_bin NOT NULL,
	`password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
	`role` varchar(255) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT USERS
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('admin', '1234', 'admin');
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('teacher','1234', 'teacher');
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('student','1234', 'student');



-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(255) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_qkjn1bn0arx376uotsywsy9bg` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT EXAM
INSERT INTO `exam`(`name`) VALUES ('exam1');



-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	subject varchar(255) COLLATE utf8_bin NOT NULL,
	item1 varchar(255) COLLATE utf8_bin NOT NULL,
	item2 varchar(255) COLLATE utf8_bin NOT NULL,
	item3 varchar(255) COLLATE utf8_bin NOT NULL,
	item4 varchar(255) COLLATE utf8_bin NOT NULL,
	answer varchar(255) COLLATE utf8_bin NOT NULL,
	exam_name varchar(255) COLLATE utf8_bin DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_eo1w6hnqd2q7o8ao851s8t13f` (`subject`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT QUESTIONS AND LINK TO EXAM1
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`, `exam_name`) VALUES ('Q1', '111', '222', '333', '444', '111', 'exam1');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`, `exam_name`) VALUES ('Q2', 'aaa', 'bbb', 'ccc', 'ddd', 'aaa', 'exam1');

SET FOREIGN_KEY_CHECKS = 1;