/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.elasticsearch.service;


import net.mingsoft.elasticsearch.bean.ESContentBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

@Service("IESContentService")
public interface IESContentService extends ElasticsearchRepository<ESContentBean, String> {

}
