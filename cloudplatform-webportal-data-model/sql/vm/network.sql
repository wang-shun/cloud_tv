CREATE TABLE `WEBPORTAL_CLOUDVM_NETWORK` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
	`REGION` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`NETWORK_ID` char(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`STATUS` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`ADMIN_STATE_UP` tinyint(4) DEFAULT NULL,
	`SHARED` tinyint(4) DEFAULT NULL,
	`TENANT_ID` char(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`NETWORK_TYPE` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`PHYSICAL_NETWORK_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`SEGMENTATION_ID` char(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `EXTERNAL` tinyint(4) DEFAULT NULL,
	`PORT_SECURITY` tinyint(4) DEFAULT NULL,
	`PROFILE_ID` char(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`MULTICAST_IP` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`SEGMENT_ADD` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`SEGMENT_DEL` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`MEMBER_SEGMENTS` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`NETWORK_FLAVOR` char(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
	`CREATED` datetime DEFAULT NULL,
  `DELETED` tinyint(4) DEFAULT NULL,
	`CREATE_TIME` datetime DEFAULT NULL,
	`UPDATE_TIME` datetime DEFAULT NULL,
	`CREATE_USER` bigint(20) unsigned DEFAULT NULL,
	`UPDATE_USER` bigint(20) unsigned DEFAULT NULL,
	PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;