/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.datascope.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.constant.e.ManagerAdminEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.datascope.annotation.DataScope;
import net.mingsoft.datascope.biz.IDataBiz;
import net.mingsoft.datascope.entity.DataEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据权限过滤
 */
@Component("dataScopeAop")
@Aspect
public class DataScopeAop extends BaseAop {

    /**
     * 注入数据权限业务层
     */
    @Autowired
    private IDataBiz dataBiz;

    @Pointcut("@annotation(net.mingsoft.datascope.annotation.DataScope)")
    public void checkPermissions() {
    }

    @Around("checkPermissions()")
    public Object checkPermissions(ProceedingJoinPoint joinPoint) throws Throwable {
        ManagerEntity manager = BasicUtil.getManager();
        //超级管理员不作权限监测
        if(ManagerAdminEnum.SUPER.toString().equals(manager.getManagerAdmin())) {
            return joinPoint.proceed();
        }
        boolean hasPermission = false;
        DataScope dataScope = this.getAnnotation(joinPoint, DataScope.class);
        Object targetObj = this.getType(joinPoint,BaseEntity.class,true);
        Object dataId = null;
        String getId = StringUtils.isNotBlank(dataScope.id())?dataScope.id():"getId";
        //targetObj 空，再判断是否是删除操作
        if(targetObj==null) {
            List list = this.getType(joinPoint, ArrayList.class);
            dataId = ReflectUtil.invoke(list.get(0),getId);
        } else { //新增操作
            dataId = ReflectUtil.invoke(targetObj,getId);
        }

        //获取对应的数据权限配置,可能是多角色
        DataEntity dataEntity = new DataEntity();
        dataEntity.setDataId(String.valueOf(dataId));
        // TODO 这里会有多角色问题
        dataEntity.setDataType(dataScope.type());
        LambdaQueryWrapper<DataEntity> wrapper = new QueryWrapper<>(dataEntity).lambda();
        wrapper.in(DataEntity::getDataTargetId, Arrays.asList(manager.getRoleIds().split(",")));
        // 获取所有的角色的数据权限
        List<DataEntity> dataList  = dataBiz.list(wrapper);
        if (CollUtil.isNotEmpty(dataList)) {
            // 遍历权限,如果有权限则hasPermission并break
            for (DataEntity data : dataList) {
                String str = data.getDataIdModel();
                if(StringUtils.isNotBlank(str)) {
                    List<Map> strJson = JSONUtil.toList(str,Map.class);
                    for(Map<String, Object> _model:strJson) {
                        //判断数据对应的功能权限
                        if(dataScope.requiresPermissions().equals(String.valueOf(_model.get("modelUrl"))) && Boolean.parseBoolean(_model.get("check").toString())) {
                            hasPermission = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!hasPermission) {
            throw new BusinessException(HttpStatus.FORBIDDEN,"无操作权限");
        } else {
            return joinPoint.proceed();
        }
    }


}
