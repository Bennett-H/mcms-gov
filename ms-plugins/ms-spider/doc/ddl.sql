CREATE TABLE IF NOT EXISTS `spider_log` (
  `id` bigint(19) NOT NULL,
  `content` text COMMENT '采集内容',
  `source_url` varchar(255) DEFAULT NULL COMMENT '内容链接',
  `imported` varchar(255) DEFAULT NULL COMMENT '是否导入过',
  `regular_id` varchar(255) DEFAULT NULL COMMENT '规则主键',
  `task_id` varchar(255) DEFAULT NULL COMMENT '任务主键',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日志表';
CREATE TABLE IF NOT EXISTS `spider_task` (
  `id` bigint(19) NOT NULL,
  `is_repeat` varchar(255) DEFAULT NULL COMMENT '是否去重',
  `is_auto_import` varchar(255) DEFAULT NULL COMMENT '自动导入',
  `import_table` varchar(255) DEFAULT NULL COMMENT '导入表',
  `task_name` varchar(255) DEFAULT NULL COMMENT '采集名称',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集任务';
CREATE TABLE IF NOT EXISTS `spider_task_regular` (
  `id` bigint(19) NOT NULL,
  `article_url` varchar(255) DEFAULT NULL COMMENT '内容链接匹配',
  `end_area` varchar(255) DEFAULT NULL COMMENT '列表结束区域',
  `start_area` varchar(255) DEFAULT NULL COMMENT '列表开始区域',
  `end_page` varchar(255) DEFAULT NULL COMMENT '结束页',
  `start_page` varchar(255) DEFAULT NULL COMMENT '起始页',
  `is_page` varchar(255) DEFAULT NULL COMMENT '是否分页',
  `link_url` varchar(255) DEFAULT NULL COMMENT '被采集页面',
  `charset` varchar(255) DEFAULT NULL COMMENT '字符编码',
  `thread_num` int(11) DEFAULT NULL COMMENT '线程数',
  `regular_name` varchar(255) DEFAULT NULL COMMENT '规则名称',
  `task_id` varchar(255) DEFAULT NULL COMMENT '任务主键',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  `meta_data` text COMMENT '元数据,存储表达式,匹配规则,名称,映射字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集规则';
