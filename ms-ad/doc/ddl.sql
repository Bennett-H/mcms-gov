CREATE TABLE IF NOT EXISTS `ad_position` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `position_template` varchar(4000) DEFAULT NULL COMMENT '广告位模板',
  `position_desc` varchar(255) DEFAULT NULL COMMENT '广告位描述',
  `position_height` int(11) DEFAULT NULL COMMENT '广告位高度',
  `position_width` int(11) DEFAULT NULL COMMENT '广告位宽度',
  `position_name` varchar(255) DEFAULT NULL COMMENT '广告位名称',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='广告位';
CREATE TABLE IF NOT EXISTS `ad_ads` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ads_people_email` varchar(255) DEFAULT NULL COMMENT '广告联系人邮箱',
  `ads_people_phone` varchar(255) DEFAULT NULL COMMENT '广告联系人电话',
  `ads_people_name` varchar(255) DEFAULT NULL COMMENT '广告联系人',
  `ads_img` varchar(4000) DEFAULT NULL COMMENT '广告缩略图',
  `ads_state` varchar(255) DEFAULT NULL COMMENT '是否开启(open:开启，close：关闭)',
  `ads_end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `ads_url` varchar(255) DEFAULT '' COMMENT '广告链接',
  `ads_start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `ads_type` varchar(255) DEFAULT NULL COMMENT '广告类型',
  `ads_name` varchar(255) DEFAULT NULL COMMENT '广告名称',
  `position_id` int(11) DEFAULT NULL COMMENT '广告位编号',
  `del` int(1) DEFAULT '0' COMMENT '删除标记',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` int(11) DEFAULT NULL COMMENT '修改人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='广告';
