CREATE TABLE IF NOT EXISTS `file_folder` (
  `ID` bigint(19) NOT NULL,
  `FOLDER_NAME` varchar(255) DEFAULT NULL COMMENT '文件夹名称',
  `FOLDER_DESCRIBE` varchar(255) DEFAULT NULL COMMENT '文件描述',
  `PATH` varchar(255) DEFAULT NULL COMMENT '文件夹路径',
  `LEAF` int(1) DEFAULT NULL COMMENT '是否是叶子节点',
  `TOP_ID` bigint(20) DEFAULT NULL COMMENT '顶级ID',
  `PARENT_ID` varchar(255) DEFAULT NULL COMMENT '父节点',
  `PARENT_IDS` varchar(255) DEFAULT NULL COMMENT '父类型编号，多个ID逗号',
  `CREATE_DATE` datetime DEFAULT NULL COMMENT '创建时间',
  `CREATE_BY` varchar(50) DEFAULT NULL COMMENT '创建人',
  `UPDATE_DATE` datetime DEFAULT NULL COMMENT '修改时间',
  `UPDATE_BY` varchar(50) DEFAULT NULL COMMENT '修改人',
  `DEL` int(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件夹表';
