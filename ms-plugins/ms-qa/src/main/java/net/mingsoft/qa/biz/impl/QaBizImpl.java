/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循铭软科技《保密协议》
 */


package net.mingsoft.qa.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.bean.ModelJsonBean;
import net.mingsoft.qa.biz.IQaBiz;
import net.mingsoft.qa.dao.IQaDao;
import net.mingsoft.qa.entity.QaEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义表单接口实现类
 *
 * @author 铭软
 * @version 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：2022-1-21 modelDao.excuteSql(sql); 只有创建表和更新表可以执行
 */
@Service("QaBizImpl")
@Transactional(rollbackFor = Exception.class)
public class QaBizImpl extends BaseBizImpl<IQaDao, QaEntity> implements IQaBiz {

    /**
     * 注入自定义表单持久化层
     */
    @Autowired
    private IQaDao qaDao;


    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return qaDao;
    }

    @Override
    public boolean importConfig(ModelJsonBean modelJsonBean) {
        if (modelJsonBean == null) {
            return false;
        }
        return this.importModel(modelJsonBean, "");
    }


    @Override
    public boolean importModel(ModelJsonBean modelJsonBean, String modelType) {
        if (modelJsonBean == null) {
            return false;
        }
        QaEntity qa = new QaEntity();
        qa.setQaName(modelJsonBean.getTitle());
        QaEntity qaEntity = super.getOne(new QueryWrapper<>(qa));
        //判断表名是否存在
        if (ObjectUtil.isNotNull(qaEntity)) {
            return false;
        }

        //在表名前面拼接前缀
        qa.setQaTableName("QA_" + modelJsonBean.getTableName());
        //创建表
        String[] formSqls = modelJsonBean.getSql().replace("{model}", "QA_").trim().split(";");
        for (String sql : formSqls) {
            //只有创建表和更新表可以执行
            if (StringUtils.isBlank(sql) || StrUtil.containsAnyIgnoreCase(sql, "INSERT ", "DELETE ", "DROP ")) {
                continue;
            }
            qaDao.excuteSql(sql);
            ArrayList<String> sqls = new ArrayList<>();
            //添加3个默认字段
            sqls.add(StrUtil.format("ALTER TABLE `{}` ADD COLUMN `qa_device_type` varchar(255) NULL COMMENT '网络设备'", qa.getQaTableName()));
            sqls.add(StrUtil.format("ALTER TABLE `{}` ADD COLUMN `qa_address` varchar(255) NULL COMMENT '网络设备'", qa.getQaTableName()));
            sqls.add(StrUtil.format("ALTER TABLE `{}` ADD COLUMN `qa_IP` varchar(255) NULL COMMENT 'IP地址'", qa.getQaTableName()));
            sqls.forEach(qaDao::excuteSql);
        }

        Map json = new HashMap();
        json.put("html", modelJsonBean.getHtml());
        json.put("searchJson", modelJsonBean.getSearchJson());
        json.put("script", modelJsonBean.getScript());
        json.put("isWebSubmit", modelJsonBean.isWebSubmit());
        qa.setModelField(modelJsonBean.getField());
        qa.setQaType(modelType);
        qa.setModelJson(JSONUtil.toJsonStr(json));
        qa.setCreateBy(BasicUtil.getManager().getId());
        qa.setCreateDate(new Date());
        qa.setUpdateDate(new Date());
        //保存自定义模型实体
        super.save(qa);
        return true;
    }

    @Override
    public boolean updateConfig(String modelId, ModelJsonBean modelJsonBean) {
        if (StringUtils.isEmpty(modelId) || modelJsonBean == null) {
            return false;
        }
        return this.updateConfig(modelId, modelJsonBean, "");
    }

    @Override
    public boolean updateConfig(String modelId, ModelJsonBean modelJsonBean, String modelType) {
        if (StringUtils.isEmpty(modelId) || modelJsonBean == null) {
            return false;
        }
        QaEntity qaEntity = super.getById(modelId);
        if (ObjectUtil.isNull(qaEntity)) {
            return false;
        }
        QaEntity qa = new QaEntity();
        qa.setQaName(modelJsonBean.getTitle());
        QaEntity oldQa = super.getOne(new QueryWrapper<>(qa));
        //判断表名是否存在
        if (ObjectUtil.isNotNull(oldQa) && !qaEntity.getId().equals(oldQa.getId())) {
            return false;
        }
        // 原始表名
        String oldTableName = qaEntity.getQaTableName();
        // 表名重命名
        String rename = "ALTER  TABLE {} RENAME TO {};";

        //在表名前面拼接前缀
        qaEntity.setQaTableName(("QA_" + modelJsonBean.getTableName()).toUpperCase());
        updateTable(qaEntity.getModelField(), modelJsonBean.getField(), qaEntity.getQaTableName());
        if (!oldTableName.equals(qaEntity.getQaTableName())) {
            rename = StrUtil.format(rename, oldTableName, qaEntity.getQaTableName());
            //只有创建表和更新表可以执行
            if (!(StringUtils.isBlank(rename) && StrUtil.containsAnyIgnoreCase(rename, "INSERT ", "DELETE ", "DROP "))) {
                qaDao.excuteSql(rename);
            }

        }

        // 更新业务信息
        Map json = new HashMap();
        json.put("html", modelJsonBean.getHtml());
        json.put("script", modelJsonBean.getScript());
        json.put("searchJson", modelJsonBean.getSearchJson());
        json.put("isWebSubmit", modelJsonBean.isWebSubmit());
        qaEntity.setModelField(modelJsonBean.getField());
        qaEntity.setQaName(modelJsonBean.getTitle());
        qaEntity.setQaType(modelType);
        qaEntity.setModelJson(JSONUtil.toJsonStr(json));
        qa.setUpdateDate(new Date());
        //保存自定义模型实体
        super.updateById(qaEntity);
        return true;
    }

    @Override
    public boolean delete(List<String> ids) {
        for (String id : ids) {
            QaEntity qaEntity = super.getById(id);
            boolean flag = super.removeById(id);
            if (!flag) {
                LOG.debug("{}删除失败", qaEntity.getQaTableName());
                break;
            } else {
                qaDao.dropTable(qaEntity.getQaTableName());
            }
        }
        return true;
    }


    private void updateTable(String oldStr, String newStr, String tableName) {
        List<Dict> oldList = JSONUtil.toList(oldStr, Dict.class);
        List<Dict> newList = JSONUtil.toList(newStr, Dict.class);
        StringBuffer stringBuffer = new StringBuffer();
        // 获取两个集合的差集
        Collection<Dict> disMap = CollUtil.disjunction(oldList, newList);
        if (CollUtil.isNotEmpty(disMap)) {
            // 旧字符串中删除和修改的集合
            Collection<Dict> oldIntersection = CollUtil.intersection(oldList, disMap);
            String alertTable = "";
            stringBuffer.append(alertTable);
            if (CollUtil.isNotEmpty(oldIntersection)) {
                String dropSql = StrUtil.format("ALTER TABLE {} ", tableName.toUpperCase()).concat("DROP COLUMN {};");
                List<String> dropList = oldIntersection.stream().map(dict -> StrUtil.format(dropSql, dict.getStr("field"))).collect(Collectors.toList());
                stringBuffer.append(CollUtil.join(dropList, ";"));
            }
            // 新字符串中新增和修改的集合
            Collection<Dict> newIntersection = CollUtil.intersection(newList, disMap);
            if (CollUtil.isNotEmpty(newIntersection)) {
                // 拼接drop和add 中间的分隔符
                if (CollUtil.isNotEmpty(oldIntersection)) {
                    stringBuffer.append(";");
                }
                String addSql = StrUtil.format("ALTER TABLE {} ", tableName.toUpperCase()).concat("ADD COLUMN {} {}({}) NULL;");
                List<String> addList = newIntersection.stream().map(dict -> StrUtil.format(addSql, dict.getStr("field"), dict.getStr("jdbcType"), dict.getStr("length"))).collect(Collectors.toList());
                stringBuffer.append(CollUtil.join(addList, ";"));
            }
            stringBuffer.append(";");
            LOG.debug("执行的SQL{}", stringBuffer);

            String[] formSqls = stringBuffer.toString().split(";");
            for (String sql : formSqls) {
                //只有创建表和更新表可以执行
                if (StringUtils.isBlank(sql) || StrUtil.containsAnyIgnoreCase(sql, "INSERT ", "DELETE ", "DROP ")) {
                    break;
                }
                qaDao.excuteSql(sql);
            }

        }


    }
}
