/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.action;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.impexp.bean.SetBean;
import net.mingsoft.impexp.biz.IImpExpBiz;
import net.mingsoft.impexp.biz.ISetBiz;
import net.mingsoft.impexp.entity.SetEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.String.valueOf;

/**
 * 导入导出Excel管理控制层
 *
 * @author 铭软科技
 * 创建日期：2021-1-27 10:58:44<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-导入导出模块接口"})
@Controller("excelAction")
@RequestMapping("/${ms.manager.path}/impexp/excel")
public class ExcelAction extends BaseFileAction {

    /**
     * 注入导入导出配置业务层
     */
    @Autowired
    private ISetBiz setBiz;

    @Autowired
    private IImpExpBiz impExpBiz;


    @ApiOperation(value = "导入Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = false, paramType = "query"),
    })
    @PostMapping("/imp")
    @ResponseBody
    public ResultData imp(UploadConfigBean bean, HttpServletRequest req, HttpServletResponse res) throws IOException {
        DefaultMultipartHttpServletRequest dmhq = (DefaultMultipartHttpServletRequest)req;

        //非法路径过滤
        if (bean.getUploadPath() != null && (bean.getUploadPath().contains("../") || bean.getUploadPath().contains("..\\"))) {
            return ResultData.build().error(getResString("err.error", new String[]{getResString("file.path")}));
        }
        // 是否需要拼接appId
        if (bean.isAppId()) {
            bean.setUploadPath(BasicUtil.getApp().getAppId() + File.separator + bean.getUploadPath());
        }

        //获取参数
        Map<String, Object> map = new HashMap<>();
        Map<String, String[]> uploadMap = dmhq.getParameterMap();
        uploadMap.entrySet().stream().forEach(entry->{
            map.put(entry.getKey(),entry.getValue()[0]);
        });

        SetEntity set = new SetEntity();
        String name = map.get("name").toString();
        if(StringUtils.isNotEmpty(name)){
            set.setName(name);
            set =  setBiz.getOne(new QueryWrapper<>(set));
            if (set == null) {
                return ResultData.build().error(getResString("err.error", this.getResString("name")));
            }
        } else {
            return ResultData.build().error(getResString("err.error", this.getResString("name")));
        }



        ResultData result = this.upload(bean);
        //读取上传后的文件路径
        //导入数据
        impExpBiz.impExcel(set, result.getData(String.class),map);
        return ResultData.build().success();
    }

    /**
     * 导出Excel
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "导出Excel")
    @PostMapping("/exp")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = true, paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "导入导出参数", required = false, paramType = "query"),
    })
    @ResponseBody
    public ResponseEntity<byte[]> exp(@RequestBody @ApiIgnore SetBean setBean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SetEntity setEntity = new SetEntity();
        LOG.debug("ids:{}" , setBean.getIds());

        if (setBean != null && StringUtils.isNotEmpty(setBean.getName())) {
            setEntity.setName(setBean.getName());
            setEntity = setBiz.getOne(new QueryWrapper<>(setEntity));
            if (setEntity == null) {
                return null;
            }
        } else {
            LOG.debug(setBean.getName() + "is null");
            return null;
        }
        String exportSql = setEntity.getExportSql();
        if (StringUtils.isNotEmpty(setBean.getIds())) {
            exportSql = StrUtil.format(exportSql, setBean.getIds());
        }
        ArrayList<LinkedHashMap<String, Object>> results = (ArrayList<LinkedHashMap<String, Object>>) setBiz.excuteSql(exportSql);
        // 通过工具类创建writer
        String path = BasicUtil.getRealPath("/temp/" + System.currentTimeMillis() + ".xlsx") ;
        ExcelWriter writer = ExcelUtil.getWriter(path);
        writer.write(results, true);
        writer.close();

        String fileName = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS")+".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", fileName);
        headers.setContentDispositionFormData("fileName",fileName);

        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(new File(path));
        } catch (IOException e) {
            LOG.error("读取压缩包错误");
            e.printStackTrace();
        }
        // 删除生成的文件
        FileUtil.del(path);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }

    protected String getResString(String key) {
        // TODO Auto-generated method stub
        String str = "";
        try {
            str = super.getResString(key);
        } catch (MissingResourceException e) {
            str = getLocaleString(key, net.mingsoft.impexp.constant.Const.RESOURCES);
        }

        return str;
    }

}
