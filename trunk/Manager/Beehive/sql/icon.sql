/*
MySQL Data Transfer
Source Host: localhost
Source Database: beehive
Target Host: localhost
Target Database: beehive
Date: 2009-4-22 9:47:26
*/
create database if not exists `beehive`;

USE `beehive`;

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for icon
-- ----------------------------
DROP TABLE IF EXISTS `icon`;

CREATE TABLE `icon` (
  `oid` bigint(20) NOT NULL auto_increment,
  `file_name` varchar(255) default NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `icon` VALUES ('1', 'power.png', 'power');
INSERT INTO `icon` VALUES ('2', 'home.png', 'home');
INSERT INTO `icon` VALUES ('3', 'music.png', 'music');
INSERT INTO `icon` VALUES ('4', 'infrared.png', 'infrared');
INSERT INTO `icon` VALUES ('5', 'disc.png', 'disc');
INSERT INTO `icon` VALUES ('6', 'fast_backward.png', 'fast_backward');
INSERT INTO `icon` VALUES ('7', 'prev.png', 'prev');
INSERT INTO `icon` VALUES ('8', 'play.png', 'play');
INSERT INTO `icon` VALUES ('9', 'next.png', 'next');
INSERT INTO `icon` VALUES ('10', 'fast_forward.png', 'fast_forward');
INSERT INTO `icon` VALUES ('11', 'magnify+.png', 'magnify+');
INSERT INTO `icon` VALUES ('12', 'magnify-.png', 'magnify-');
INSERT INTO `icon` VALUES ('13', 'vol_up.png', 'vol_up');
INSERT INTO `icon` VALUES ('14', 'vol_down.png', 'vol_down');
INSERT INTO `icon` VALUES ('15', 'pause.png', 'pause');
INSERT INTO `icon` VALUES ('16', 'up.png', 'up');
INSERT INTO `icon` VALUES ('17', 'down.png', 'down');
INSERT INTO `icon` VALUES ('18', 'left.png', 'left');
INSERT INTO `icon` VALUES ('19', 'right.png', 'right');
INSERT INTO `icon` VALUES ('20', 'ok.png', 'ok');
INSERT INTO `icon` VALUES ('21', 'menu.png', 'menu');
INSERT INTO `icon` VALUES ('22', 'music_balance.png', 'music_balance');
INSERT INTO `icon` VALUES ('23', 'refresh.png', 'refresh');
INSERT INTO `icon` VALUES ('24', 'back.png', 'back');
INSERT INTO `icon` VALUES ('25', 'mute.png', 'mute');
