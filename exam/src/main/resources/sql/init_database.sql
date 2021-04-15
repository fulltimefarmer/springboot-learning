SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
	`id` int NOT NULL AUTO_INCREMENT,
	`username` varchar(100) COLLATE utf8_bin NOT NULL,
	`password` varchar(100) COLLATE utf8_bin DEFAULT NULL,
	`role` varchar(30) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT USERS
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('admin', '1234', 'admin');
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('teacher','1234', 'teacher');
INSERT INTO `user`(`username`, `password`, `role`) VALUES ('student','1234', 'student');



-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
	`id` int NOT NULL AUTO_INCREMENT,
	`subject` varchar(255) COLLATE utf8_bin NOT NULL,
	`item1` varchar(255) COLLATE utf8_bin NOT NULL,
	`item2` varchar(255) COLLATE utf8_bin NOT NULL,
	`item3` varchar(255) COLLATE utf8_bin NOT NULL,
	`item4` varchar(255) COLLATE utf8_bin NOT NULL,
	`answer` varchar(255) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UK_eo1w6hnqd2q7o8ao851s8t13f` (`subject`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT QUESTIONS AND LINK TO EXAM1
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which city it the capital of Belgium?', 'Vienna', 'Brussels', 'Bragg', 'Gottingen', 'Brussels');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which is the highest mountain on earth?', 'Mount Everest', 'Kangchenjunga', 'Lhotse', 'Makalu', 'Mount Everest');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which of below is not below to seven wonders of the ancient world？', 'The Great Pyramid of Giza', 'Hanging Gardens of Babylon', 'Great Wall of China', 'Statue of Zeus at Olympia', 'Great Wall of China');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Who is the father of Linux System?', 'Bill Gates', 'Steve Jobs', 'Larry Page', 'Linus Torvalds', 'Linus Torvalds');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Who is the father of information theory?', 'Claude Shannon', 'John von Neumann', 'Alan Turing', 'Nikola Tesla', 'Claude Shannon');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Whcih of the following sort algorithms is stable?', 'quick sort', 'merge sort', 'selection sort', 'heap sort', 'quick sort');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which country is the champion of World CUP 2014?', 'Germany', 'Italy', 'Spain', 'France', 'Germany');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Who was not president of USA?', 'Thomas Jefferson', 'James Manroe', 'Andrew Jackson', 'Benjamin Franklin', 'Benjamin Franklin');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which animal can pass the mirror test?', 'cat', 'dog', 'money', 'magpie', 'magpie');
INSERT INTO `question`(`subject`, `item1`, `item2`, `item3`, `item4`, `answer`) VALUES ('Which is the biggest planet in the solar system?', 'Mercury', 'Venus', 'Jupiter', 'Saturn', 'Jupiter');


-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam` (
	`id` int NOT NULL AUTO_INCREMENT,
	`username` varchar(100) COLLATE utf8_bin NOT NULL,
	`score` varchar(30) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
	`id` int NOT NULL AUTO_INCREMENT,
	`exam_id` int COLLATE utf8_bin NOT NULL,
	`question_id` int COLLATE utf8_bin NOT NULL,
	`correct_answer` varchar(255) COLLATE utf8_bin NOT NULL,
	`student_answer` varchar(255) COLLATE utf8_bin NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;