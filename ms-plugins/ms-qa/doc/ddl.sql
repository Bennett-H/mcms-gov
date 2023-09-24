CREATE TABLE IF NOT EXISTS `qa` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `QA_NAME` varchar(255) DEFAULT NULL COMMENT '投票名称',
  `QA_TABLE_NAME` varchar(255) DEFAULT NULL COMMENT '投票表名',
  `QA_TYPE` varchar(255) DEFAULT NULL COMMENT '投票类型',
  `MODEL_JSON` text COMMENT 'json',
  `MODEL_FIELD` text COMMENT '自定义字段',
  `answer_limit` varchar(255) DEFAULT NULL COMMENT '每个ip的答题限制',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `CREATE_DATE` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_BY` varchar(50) DEFAULT NULL COMMENT '创建人',
  `UPDATE_DATE` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_BY` varchar(50) DEFAULT NULL COMMENT '修改人',
  `DEL` int(1) DEFAULT '0' COMMENT '删除标记',
  `not_del` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='调查问卷中间表';
CREATE TABLE IF NOT EXISTS `qa_game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `GAMES` varchar(255) DEFAULT NULL COMMENT '您平常玩什么游戏?',
  `GAME_TYPE` varchar(255) DEFAULT NULL COMMENT '您平常喜欢什么类型的游戏',
  `LONG_TIME` varchar(255) DEFAULT NULL COMMENT '您一周的游玩时间大概多长',
  `IS_MONEY` varchar(255) DEFAULT NULL COMMENT '您是否愿意为游戏充值',
  `MONEY_LONG` varchar(255) DEFAULT NULL COMMENT '如果愿意接受充值的仅额是多少',
  `LINK_ID` bigint(20) DEFAULT NULL,
  `CREATE_DATE` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_BY` varchar(50) DEFAULT NULL COMMENT '创建人',
  `UPDATE_DATE` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_BY` varchar(50) DEFAULT NULL COMMENT '修改人',
  `DEL` int(1) DEFAULT '0' COMMENT '删除标记',
  `qa_device_type` varchar(255) DEFAULT NULL COMMENT '网络设备',
  `qa_address` varchar(255) DEFAULT NULL COMMENT '网络设备',
  `qa_IP` varchar(255) DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='游戏调查';
