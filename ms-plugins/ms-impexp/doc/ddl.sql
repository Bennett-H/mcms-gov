CREATE TABLE IF NOT EXISTS `impexp_set` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `import_json` varchar(4000) DEFAULT NULL COMMENT '导入主表json',
  `export_sql` varchar(4000) DEFAULT NULL COMMENT '导出sql配置',
  `name` varchar(255) DEFAULT NULL COMMENT '导入导出标识',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='导入导出配置';
