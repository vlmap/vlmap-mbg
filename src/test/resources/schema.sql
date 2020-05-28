 
-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `n_id` int(11) NOT NULL AUTO_INCREMENT,
  `s_passport` varchar(255) NOT NULL,
  `s_password` varchar(255) NOT NULL,
  `s_roles` varchar(255) DEFAULT NULL,
  `s_permissions` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`n_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 

-- ----------------------------
-- Table structure for t_student
-- ----------------------------
DROP TABLE IF EXISTS `t_student`;
CREATE TABLE `t_student` (
  `n_id` bigint(20) NOT NULL COMMENT '主键ID',
  `s_name` varchar(30) DEFAULT NULL COMMENT '姓名',
  `n_age` int(11) DEFAULT NULL COMMENT '年龄',
  `s_email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`n_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 
-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(30) DEFAULT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 
