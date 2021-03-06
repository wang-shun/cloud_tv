/*Table structure for table `WEBPORTAL_APP_H5_PAGE` */

DROP TABLE IF EXISTS `WEBPORTAL_APP_H5_PAGE`;

CREATE TABLE `WEBPORTAL_APP_H5_PAGE` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'h5页面信息表主键',
  `PAGE_ID` varchar(10) DEFAULT NULL COMMENT '页面ID',
  `TYPE` char(2) DEFAULT NULL COMMENT '页面类型',
  `WEB_URL` varchar(100) DEFAULT NULL COMMENT 'Html5页面访问位置',
  `TITLE` varchar(50) DEFAULT NULL COMMENT '应用栏标题',
  `ICON_URL` varchar(100) DEFAULT NULL COMMENT '图标url',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='h5页面信息表';

/*Data for the table `WEBPORTAL_APP_H5_PAGE` */

insert  into `WEBPORTAL_APP_H5_PAGE`(`ID`,`PAGE_ID`,`TYPE`,`WEB_URL`,`TITLE`,`ICON_URL`,`DESCN`,`DELETED`,`CREATE_TIME`,`UPDATE_TIME`,`CREATE_USER`,`UPDATE_USER`) values (1,'10001','0','/h5/faultClusterList.html','故障列表','',NULL,NULL,NULL,NULL,NULL,NULL),(2,'10002','0','/h5/faultNodeList.html','故障列表','',NULL,NULL,NULL,NULL,NULL,NULL),(3,'10003','0','/h5/faultdbList.html','故障列表','',NULL,NULL,NULL,NULL,NULL,NULL),(4,'10004','0','/h5/unCheckDb.html','待审核数据库详情','',NULL,NULL,NULL,NULL,NULL,NULL),(5,'10005','0','/h5/hclusterList.html','物理机集群','drawable://ic_service_hcluster.png',NULL,NULL,NULL,NULL,NULL,NULL),(6,'10006','0','/h5/RDSMclusterList.html','RDS管理','drawable://ic_service_rds.png',NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `WEBPORTAL_APP_SUGGESTION` */

DROP TABLE IF EXISTS `WEBPORTAL_APP_SUGGESTION`;

CREATE TABLE `WEBPORTAL_APP_SUGGESTION` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '反馈建议表主键',
  `CONTACT` varchar(30) DEFAULT NULL COMMENT '邮箱或电话',
  `CONTENT` varchar(500) DEFAULT NULL COMMENT '内容',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='反馈建议表';


/*Table structure for table `WEBPORTAL_APP_VERSION` */

DROP TABLE IF EXISTS `WEBPORTAL_APP_VERSION`;

CREATE TABLE `WEBPORTAL_APP_VERSION` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '版本表主键',
  `VERSION_CODE` int(10) DEFAULT NULL COMMENT '版本编号',
  `VERSION_NAME` varchar(20) DEFAULT NULL COMMENT '版本名称',
  `PLATFORM` varchar(20) DEFAULT NULL COMMENT '平台',
  `FORCE_UPDATE` tinyint(1) DEFAULT NULL COMMENT '是否强制升级：1-是，0-否',
  `URL` varchar(200) DEFAULT NULL COMMENT '下载地址',
  `DESCN` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL,
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='版本表';

ALTER TABLE `WEBPORTAL_USER` ADD COLUMN `PHONE` VARCHAR(20) DEFAULT NULL COMMENT '电话';
ALTER TABLE `WEBPORTAL_USER` ADD COLUMN `ICON_URL` VARCHAR(200) DEFAULT NULL COMMENT '头像地址';

CREATE TABLE `WEBPORTAL_APP_MESSAGE` (
  `ID` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息通知表主键',
  `TITLE` VARCHAR(50) DEFAULT NULL COMMENT '消息名称',
  `DESCN` VARCHAR(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `DELETED` TINYINT(4) DEFAULT NULL,
  `CREATE_TIME` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` DATETIME DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` BIGINT(20) UNSIGNED DEFAULT NULL,
  `UPDATE_USER` BIGINT(20) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='消息通知表';