/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.mdiy.biz.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.biz.impl.BaseBizImpl;
import net.mingsoft.base.dao.IBaseDao;
import net.mingsoft.mdiy.bean.ModelJsonBean;
import net.mingsoft.mdiy.biz.IConfigBiz;
import net.mingsoft.mdiy.biz.IModelBiz;
import net.mingsoft.mdiy.constant.e.ModelCustomTypeEnum;
import net.mingsoft.mdiy.dao.IModelDao;
import net.mingsoft.mdiy.entity.ConfigEntity;
import net.mingsoft.mdiy.entity.ModelEntity;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 自定义表单接口实现类
 *
 * @author 铭软
 * @version 版本号：100-000-000<br/>
 * 创建日期：2012-03-15<br/>
 * 历史修订：2022-1-21 modelDao.excuteSql(sql); 只有创建表和更新表可以执行
 */
@Service("mdiyModelBizImpl")
@Transactional(rollbackFor = Exception.class)
public class ModelBizImpl extends BaseBizImpl<IModelDao, ModelEntity> implements IModelBiz {

    /**
     * 注入自定义表单持久化层
     */
    @Autowired
    private IModelDao modelDao;

    @Autowired
    private IConfigBiz configBiz;


    @Override
    protected IBaseDao getDao() {
        // TODO Auto-generated method stub
        return modelDao;
    }

    @Override
    public boolean importConfig(String customType, ModelJsonBean modelJsonBean) {
        if (StringUtils.isEmpty(customType) || modelJsonBean == null) {
            return false;
        }
        return this.importModel(customType, modelJsonBean, "");
    }


    @Override
    public boolean importModel(String customType, ModelJsonBean modelJsonBean, String modelType) {
        if (StringUtils.isEmpty(customType) || modelJsonBean == null) {
            return false;
        }
        if (StringUtils.isBlank(modelJsonBean.getTitle())){
            return false;
        }
        // 判断导入的模型业务类型一致的情况下，判断模型名 或 表名是否存在
        LambdaQueryWrapper<ModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(customType),ModelEntity::getModelCustomType,customType)
                .and(wrapper -> wrapper.eq(ModelEntity::getModelName,modelJsonBean.getTitle())
                        .or().eq(StrUtil.isNotBlank(modelJsonBean.getTableName()),ModelEntity::getModelTableName,"mdiy_"+customType+"_"+modelJsonBean.getTableName()));

        List<ModelEntity> modelEntities = this.list(queryWrapper);
        //判断表名是否存在
        if (CollectionUtil.isNotEmpty(modelEntities)) {
            return false;
        }
        ModelEntity model = new ModelEntity();
        model.setModelName(modelJsonBean.getTitle());
        model.setModelTableName(modelJsonBean.getTableName());
        model.setModelCustomType(customType);
        if (customType.equalsIgnoreCase(ModelCustomTypeEnum.MODEL.getLabel())) {
            //在表名前面拼接前缀
            model.setModelTableName("MDIY_MODEL_" + modelJsonBean.getTableName());
            //创建表
            if (StringUtils.isNotBlank(modelJsonBean.getSql())){
                String[] modelSqls = modelJsonBean.getSql().replace("{model}", "MDIY_MODEL_").trim().split(";");
                for (String sql : modelSqls) {
                    //insert、delete和DROP TABLE 语句不能执行
                    if (StringUtils.isBlank(sql) || StrUtil.containsAnyIgnoreCase(sql,"INSERT ","SELECT ","UPDATE ","DELETE ", "DROP ", "ALTER ", "TRUNCATE ","RENAME ")) {
                        continue;
                    }
                    // 只允许创建、修改当前导入模型名称的表
                    String createRegex = "CREATE[\\s]*TABLE[\\s]*`[\\s]*MDIY_MODEL_"+ modelJsonBean.getTableName().toUpperCase()+"[\\s]*`";
                    String alterRegex = "ALTER[\\s]*TABLE[\\s]*`[\\s]*MDIY_MODEL_"+ modelJsonBean.getTableName().toUpperCase()+"[\\s]*`";
                    Pattern createPattern = Pattern.compile(createRegex);
                    Pattern alterPattern = Pattern.compile(alterRegex);
                    if (!createPattern.matcher(sql).find() && !alterPattern.matcher(sql).find()){
                        continue;
                    }
                    modelDao.excuteSql(sql);
                }
            }
        }
        if (customType.equalsIgnoreCase(ModelCustomTypeEnum.FORM.getLabel())) {
            //在表名前面拼接前缀
            model.setModelTableName("MDIY_FORM_" + modelJsonBean.getTableName());
            // 自定义业务移除link_id
            // 批注：这里可能还会进行优化——使用正则来进行替换,这样的好处是代码生成器字段有变化的时候不会影响这里
            modelJsonBean.setSql(modelJsonBean.getSql().replaceAll("\\W?LINK_ID\\W? \\w+\\(\\d+\\) DEFAULT NULL,",""));
            modelJsonBean.setSql(modelJsonBean.getSql().replaceAll("ALTER TABLE.*UNIQUE.*;",""));

            //创建表
            String[] formSqls = modelJsonBean.getSql().replace("{model}", "MDIY_FORM_").trim().split(";");
            for (String sql : formSqls) {
                //insert、delete和DROP TABLE 语句不能执行 // 跳过link_id
                if (StringUtils.isBlank(sql) || StrUtil.containsAnyIgnoreCase(sql,"INSERT ","SELECT ","UPDATE ","DELETE ", "DROP ", "ALTER ", "TRUNCATE ","RENAME ", "UNIQUE_LINK_ID`(`LINK_ID`)", "UNIQUE(\"LINK_ID\")", "DROP TABLE ")) {
                    //oracle会存在create里面有select，具体看代码生成器
                    if(sql.indexOf("CREATE") < 0) {
                        continue;
                    }
                }
                // 只允许创建当前导入模型名称的表
                String regex = "CREATE[\\s]*TABLE[\\s]*`[\\s]*MDIY_FORM_"+ modelJsonBean.getTableName().toUpperCase()+"[\\s]*`";
                Pattern pattern = Pattern.compile(regex);
                if (!pattern.matcher(sql).find()){
                    continue;
                }
                //oracle需要分号结束
                sql = sql.replace("FROM dual","FROM dual;").replace(" END"," END;");
                try {
                    modelDao.excuteSql(sql);
                } catch (Exception e){
                    LOG.debug("恶意执行 sql: {}",sql);
                    e.printStackTrace();

                }

                LOG.debug(sql);
            }
        }
        Map json = new HashMap();
        json.put("html", modelJsonBean.getHtml());
        json.put("searchJson", modelJsonBean.getSearchJson());
        json.put("script", modelJsonBean.getScript());
        json.put("isWebSubmit", modelJsonBean.isWebSubmit());
        json.put("isWebCode", modelJsonBean.isWebCode());
        json.put("sql", modelJsonBean.getSql());
        json.put("tableName", modelJsonBean.getTableName());
        model.setModelField(modelJsonBean.getField());
        model.setModelType(modelType);
        model.setModelJson(JSONUtil.toJsonStr(json));
        //保存自定义模型实体
        super.save(model);
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
        ModelEntity modelEntity = super.getById(modelId);
        if (ObjectUtil.isNull(modelEntity)) {
            return false;
        }
        ModelEntity model = new ModelEntity();
        model.setModelName(modelJsonBean.getTitle());
        model.setModelCustomType(modelEntity.getModelCustomType());
        ModelEntity oldModel = super.getOne(new QueryWrapper<>(model));
        //判断表名是否存在
        if (ObjectUtil.isNotNull(oldModel) && !modelEntity.getId().equals(oldModel.getId())) {
            return false;
        }
        // 原始表名
        String oldTableName = modelEntity.getModelTableName();
        // 表名重命名
        String rename = "ALTER  TABLE {} RENAME TO {};";
        if (modelEntity.getModelCustomType().equalsIgnoreCase(ModelCustomTypeEnum.MODEL.getLabel())) {
            //在表名前面拼接前缀
            modelEntity.setModelTableName(("MDIY_MODEL_" + modelJsonBean.getTableName()).toUpperCase());
            updateTable(modelEntity.getModelField(), modelJsonBean.getField(), modelEntity.getModelTableName());

            if (!oldTableName.equals(modelEntity.getModelTableName())) {
                rename = StrUtil.format(rename, oldTableName, modelEntity.getModelTableName());
                //insert、delete和DROP TABLE 语句不能执行
                if (StringUtils.isNotBlank(rename) && !StrUtil.containsAnyIgnoreCase(rename,"INSERT ","SELECT ","UPDATE ","DELETE ", "DROP ", "TRUNCATE ","RENAME ")) {
                    modelDao.excuteSql(rename);
                }
            }
        }
        if (modelEntity.getModelCustomType().equalsIgnoreCase(ModelCustomTypeEnum.FORM.getLabel())) {
            //在表名前面拼接前缀
            modelEntity.setModelTableName(("MDIY_FORM_" + modelJsonBean.getTableName()).toUpperCase());
            updateTable(modelEntity.getModelField(), modelJsonBean.getField(), modelEntity.getModelTableName());
            if (!oldTableName.equals(modelEntity.getModelTableName())) {
                rename = StrUtil.format(rename, oldTableName, modelEntity.getModelTableName());
                ///insert、delete和DROP TABLE 语句不能执行
                if (StringUtils.isNotBlank(rename) && !StrUtil.containsAnyIgnoreCase(rename,"INSERT ","SELECT ","UPDATE ","DELETE ", "DROP ", "TRUNCATE ","RENAME ")) {
                    modelDao.excuteSql(rename);
                }
            }
        }
        // 更新业务信息
        Map json = new HashMap();
        json.put("html", modelJsonBean.getHtml());
        json.put("searchJson", modelJsonBean.getSearchJson());
        json.put("script", modelJsonBean.getScript());
        json.put("isWebSubmit", modelJsonBean.isWebSubmit());
        json.put("isWebCode", modelJsonBean.isWebCode());
        json.put("sql", modelJsonBean.getSql());
        json.put("tableName", modelJsonBean.getTableName());
        modelEntity.setModelField(modelJsonBean.getField());
        modelEntity.setModelName(modelJsonBean.getTitle());
        modelEntity.setModelType(modelType);
        modelEntity.setModelJson(JSONUtil.toJsonStr(json));
        //保存自定义模型实体
        super.updateById(modelEntity);
        List<Map> mapList = JSONUtil.toList(JSONUtil.parseArray(modelJsonBean.getField()), Map.class);
        List<String> fieldList = mapList.stream().map(map -> StrUtil.toCamelCase(map.get("field").toString().toLowerCase())).collect(Collectors.toList());
        fieldList.add("linkId");
        fieldList.add("modelId");
        ConfigEntity configEntity =  configBiz.getOne(new QueryWrapper<ConfigEntity>().eq("config_name",modelEntity.getModelName()));
        Map<String, Object> map = ConfigUtil.getMap(modelEntity.getModelName());

        if (CollUtil.isNotEmpty(map)) {
            Object[] keys = map.keySet().toArray();
            for (Object key : keys) {
                if (!fieldList.contains(key)){
                    map.remove(key);
                }
            }
            configEntity.setConfigData(JSONUtil.toJsonStr(map));
            configBiz.updateById(configEntity);
        }
        return true;
    }

    /**
     * 批量删除，并且删除表
     * @param ids
     * @return
     */
    @Override
    public boolean delete(List<String> ids) {
        for (String id : ids) {
            ModelEntity modelEntity = super.getById(id);
            boolean flag = super.removeById(id);
            if (!flag) {
                LOG.debug("{}删除失败", modelEntity.getModelTableName());
                break;
            } else {
                try {
                    modelDao.dropTable(modelEntity.getModelTableName());
                } catch (Exception e){
                    LOG.debug("{}表不存在", modelEntity.getModelTableName());
                    e.printStackTrace();
                    continue;
                }
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
                List<String> addList = newIntersection.stream().map(dict -> StrUtil.format(addSql, dict.getStr("field"), dict.getStr("jdbcType"), dict.getStr("length")).replaceAll("\\(0\\)","")).collect(Collectors.toList());
                stringBuffer.append(CollUtil.join(addList, ";"));
            }
            stringBuffer.append(";");
            LOG.debug("执行的SQL{}", stringBuffer);

            String[] formSqls = stringBuffer.toString().split(";");
            for (String sql : formSqls) {
                //insert和delete语句不能执行
                if (StringUtils.isBlank(sql) || StrUtil.containsAnyIgnoreCase(sql,"SELECT ","INSERT ","DELETE ","DELETE ")) {
                    continue;
                }
                modelDao.excuteSql(sql);
            }

        }


    }
}
