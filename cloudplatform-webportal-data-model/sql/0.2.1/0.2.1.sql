/*
 * 新增gce容器扩展表，存储gce内外网映射端口
 */
DROP TABLE WEBPORTAL_GCECONTAINER_EXT;
CREATE TABLE `WEBPORTAL_GCECONTAINER_EXT` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'container扩展表主键',
  `CONTAINER_ID` bigint(20) DEFAULT NULL COMMENT 'GCE容器ID',
  `BIND_PORT` varchar(10) DEFAULT NULL COMMENT '绑定端口',
  `INNER_PORT` varchar(10) DEFAULT NULL COMMENT '内部端口',
  `DELETED` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  `TYPE` varchar(20) DEFAULT NULL COMMENT '类型',
  `DESCN` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '创建人ID',
  `UPDATE_USER` bigint(20) unsigned DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='container扩展表';

CREATE TABLE `WEBPORTAL_GCE_EXT` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `GCE_ID` bigint(20) unsigned DEFAULT NULL,
  `RDS_ID` bigint(20) unsigned DEFAULT NULL,
  `OCS_ID` bigint(20) unsigned DEFAULT NULL,
  `DELETED` tinyint(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CREATE_USER` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


LOCK TABLES `WEBPORTAL_TEMPLATE_TASK` WRITE;
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK` DISABLE KEYS */;
INSERT INTO `WEBPORTAL_TEMPLATE_TASK` VALUES 
(14,'GCE_BUY_V002','GCE','GCE_BUY_V002',0,'2015-05-07 11:02:13',NULL,NULL,NULL);
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `WEBPORTAL_TEMPLATE_TASK_DETAIL` WRITE;
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK_DETAIL` DISABLE KEYS */;
INSERT INTO `WEBPORTAL_TEMPLATE_TASK_DETAIL` VALUES
(77,'启动gblancer代理','启动gblancer代理','taskGceContainerStartGbalancerService','GCE',NULL,1,0,0,'2015-07-13 14:04:53',NULL,NULL,NULL),
(78,'启动moxi代理','启动moxi代理','taskGceContainerStartMoxiService','GCE',NULL,1,0,0,'2015-07-13 14:05:24',NULL,NULL,NULL);
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK_DETAIL` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `WEBPORTAL_TEMPLATE_TASK_CHAIN` WRITE;
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK_CHAIN` DISABLE KEYS */;
INSERT INTO `WEBPORTAL_TEMPLATE_TASK_CHAIN` VALUES
(194,14,69,1,1,0,'2015-07-13 13:58:45',NULL,NULL,NULL),
(195,14,70,2,1,0,'2015-07-13 13:58:58',NULL,NULL,NULL),
(196,14,71,3,1,0,'2015-07-13 13:59:06',NULL,NULL,NULL),
(197,14,72,4,1,0,'2015-07-13 13:59:17',NULL,NULL,NULL),
(198,14,73,5,1,0,'2015-07-13 13:59:24',NULL,NULL,NULL),
(199,14,74,6,1,0,'2015-07-13 13:59:33',NULL,NULL,NULL),
(200,14,75,7,1,0,'2015-07-13 13:59:41',NULL,NULL,NULL),
(201,14,29,8,1,0,'2015-07-13 13:59:50',NULL,NULL,NULL),
(202,14,30,9,1,0,'2015-07-13 13:59:56',NULL,NULL,NULL),
(203,14,31,10,1,0,'2015-07-13 14:00:02',NULL,NULL,NULL),
(204,14,32,11,1,0,'2015-07-13 14:00:10',NULL,NULL,NULL),
(205,14,33,12,1,0,'2015-07-13 14:00:16',NULL,NULL,NULL),
(206,14,34,13,1,0,'2015-07-13 14:00:29',NULL,NULL,NULL),
(207,14,35,14,1,0,'2015-07-13 14:00:34',NULL,NULL,NULL),
(208,14,36,15,1,0,'2015-07-13 14:00:39',NULL,NULL,NULL),
(209,14,65,16,1,0,'2015-07-13 14:00:51',NULL,NULL,NULL),
(210,14,67,17,1,0,'2015-07-13 14:00:58',NULL,NULL,NULL),
(211,14,66,18,1,0,'2015-07-13 14:01:12',NULL,NULL,NULL),
(212,14,68,19,1,0,'2015-07-13 14:01:20',NULL,NULL,NULL),
(214,14,77,20,1,0,'2015-07-13 14:05:46',NULL,NULL,NULL),
(215,14,78,21,1,0,'2015-07-13 14:06:31',NULL,NULL,NULL),
(216,14,37,22,1,0,'2015-07-13 14:07:07',NULL,NULL,NULL),
(217,14,38,23,1,0,'2015-07-13 14:07:15',NULL,NULL,NULL),
(218,14,50,24,1,0,'2015-07-13 14:07:25',NULL,NULL,NULL);
/*!40000 ALTER TABLE `WEBPORTAL_TEMPLATE_TASK_CHAIN` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

