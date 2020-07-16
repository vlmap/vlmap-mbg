/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : mybatis

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2020-07-16 18:01:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for hbm_tab_sample
-- ----------------------------
DROP TABLE IF EXISTS `hbm_tab_sample`;
CREATE TABLE `hbm_tab_sample` (
  `a` int(128) NOT NULL AUTO_INCREMENT,
  `b` varchar(128) DEFAULT NULL,
  `c` varchar(255) DEFAULT NULL,
  `d` varchar(255) DEFAULT NULL,
  `e` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`a`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for hbm_tab_embedded
-- ----------------------------
DROP TABLE IF EXISTS `hbm_tab_embedded`;
CREATE TABLE `hbm_tab_embedded` (
  `a` varchar(128) NOT NULL,
  `b` varchar(128) NOT NULL,
  `c` varchar(255) DEFAULT NULL,
  `d` varchar(255) DEFAULT NULL,
  `e` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`a`,`b`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for hbm_tab_idclass
-- ----------------------------
DROP TABLE IF EXISTS `hbm_tab_idclass`;
CREATE TABLE `hbm_tab_idclass` (
  `a` varchar(128) NOT NULL,
  `b` varchar(128) NOT NULL,
  `c` varchar(255) DEFAULT NULL,
  `d` varchar(255) DEFAULT NULL,
  `e` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`a`,`b`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

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
