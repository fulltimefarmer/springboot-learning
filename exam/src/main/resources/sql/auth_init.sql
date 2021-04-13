SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

BEGIN;

-- projects
INSERT INTO `gcsc_project` VALUES (1,'common','common service');
INSERT INTO `gcsc_project` VALUES (2,'lastmile','last mile');
INSERT INTO `gcsc_project` VALUES (3,'touchless','touchless');
INSERT INTO `gcsc_project` VALUES (4,'cig','cig');
INSERT INTO `gcsc_project` VALUES (5,'lbp','lbp');
INSERT INTO `gcsc_project` VALUES (6,'topdoor','top door');
INSERT INTO `gcsc_project` VALUES (7,'vas','vas code');
INSERT INTO `gcsc_project` VALUES (8,'ctm','ctm');
INSERT INTO `gcsc_project` VALUES (9, 'goal-mobile', 'goal mobile');

-- OKTA for dev
INSERT INTO `okta` VALUES (1, 'cig login', 'nike.gcsc.cig', 'gcsc', 'Approved', 'wMySGICc4LtWawQXu9uc2IdLKOK5NHz9IshqgtTQYVAfEG9r0K4Hqw8K30a_KPwZ', 'http://localhost:3000/implicit/callback', 'Implicit;Refresh Token;Authorization Code', 'web', NULL, 4);
-- OKTA for prod
--INSERT INTO `okta` VALUES (1, 'cig login', 'nike.gcsc.cig', 'gcsc', 'Approved', 'uDSNEhG2ztDnHyIEbMbwlqQAv8Kcq2RQC4gsXOe65bKuGjhj5fqfOTew7Fn4XaW2', 'https://cig.nike.com/implicit/callback', 'Implicit;Refresh Token;Authorization Code', 'web', NULL, 4);

-- user for dev
INSERT INTO `gcsc_user` VALUES (1, 2, 'admin', '7b0243b9c9dddbdcb7253314e99edfce', '', 'admin', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `gcsc_user` VALUES (2, 2, 'test', '7b0243b9c9dddbdcb7253314e99edfce', '', 'test', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- user for prod
--INSERT INTO `gcsc_user` VALUES (1, 2, 'admin', 'fab0dbcd7d51c8e86ad35355e85d600c', '', 'admin', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- group
INSERT INTO `gcsc_group` VALUES (1, 'admin', NULL, 1);
INSERT INTO `gcsc_group` VALUES (2, 'testA', NULL, 1);
INSERT INTO `gcsc_group` VALUES (3, 'testB', NULL, 1);

-- permission
INSERT INTO `gcsc_permission` VALUES (1, 'admin', 0, 'ALL', '^.*$', '', 1);
INSERT INTO `gcsc_permission` VALUES (2, 'testAll', 0, 'ALL', '^/move/auth/test/api/greeting.*', '', 1);
INSERT INTO `gcsc_permission` VALUES (3, 'testGet', 0, 'GET', '^/move/auth/test/api/greeting.*', '', 1);
INSERT INTO `gcsc_permission` VALUES (4, 'testPost', 0, 'POST', '^/move/auth/test/api/greeting.*', '', 1);
INSERT INTO `gcsc_permission` VALUES (5, 'testPut', 0, 'PUT', '^/move/auth/test/api/greeting.*', '', 1);
INSERT INTO `gcsc_permission` VALUES (6, 'testDelete', 0, 'DELETE', '^/move/auth/test/api/greeting.*', '', 1);

-- middle_group_user
INSERT INTO `middle_group_user` VALUES (1, 1, 1);
INSERT INTO `middle_group_user` VALUES (2, 3, 2);

-- middle_group_permission
INSERT INTO `middle_group_permission` VALUES (1, 1, 1);
INSERT INTO `middle_group_permission` VALUES (2, 2, 2);
INSERT INTO `middle_group_permission` VALUES (3, 3, 3);
INSERT INTO `middle_group_permission` VALUES (4, 3, 4);

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
