/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.action;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.base.util.BundleUtil;
import net.mingsoft.basic.action.BaseFileAction;
import net.mingsoft.basic.bean.UploadConfigBean;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.config.MSProperties;
import net.mingsoft.impexp.bean.SetBean;
import net.mingsoft.impexp.biz.IImpExpBiz;
import net.mingsoft.impexp.biz.ISetBiz;
import net.mingsoft.impexp.entity.SetEntity;
import net.mingsoft.impexp.util.WordUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 * 导入导出word管理控制层
 *
 * @author 铭软科技
 * 创建日期：2021-1-27 10:58:44<br/>
 * 历史修订：<br/>
 */
@Api(tags = {"后端-导入导出模块接口"})
@Controller("wordAction")
@RequestMapping("/${ms.manager.path}/impexp/word")
public class WordAction extends BaseFileAction {

    public static final Logger LOGGER = LoggerFactory.getLogger(WordAction.class);

    /**
     * 注入导入导出配置业务层
     */
    @Autowired
    private ISetBiz setBiz;

    @Autowired
    private IImpExpBiz impExpBiz;


	/**
	 * 导入
	 * @param bean
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	@ApiOperation(value = "导入Word")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "docFileName", value = "导入导出标识", required =false,paramType="query"),
	})
	@PostMapping("/imp")
	@ResponseBody
	public ResultData imp(UploadConfigBean bean, HttpServletRequest req, HttpServletResponse res) throws IOException, TransformerException, ParserConfigurationException {
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
        }else {
            return ResultData.build().error(getResString("err.error", this.getResString("name")));
        }
        if(set == null ){
            return ResultData.build().error(getResString("err.error", this.getResString("name")));
        }

        LOGGER.debug("保存上传临时文件:{},大小:{}",bean.getFile().getOriginalFilename(),bean.getFile().getSize());
        ResultData result = this.upload(bean);
        //获取上传文件
        String filePath = BasicUtil.getRealPath(result.getData(String.class));
        // 用于获取doc文件名,这里做为形参传递替换importjson 中的columns
        boolean flag =  impExpBiz.impWord(set, filePath, map.get("imgDir").toString() ,map);
        //del doc
        LOGGER.debug("删除临时文件:{}",bean.getFile().getOriginalFilename());
        FileUtil.del(filePath);
        if(flag){
            return ResultData.build().success();
        }else {
            return ResultData.build().error(getResString("err.error",BundleUtil.getString(net.mingsoft.impexp.constant.Const.RESOURCES,"import")));
        }
	}

    /**
     * 导出word
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "导出Word")
    @PostMapping("/exp")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "导入导出标识", required = true, paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "导入导出参数", required = false, paramType = "query"),
    })
    @ResponseBody
    public ResponseEntity<byte[]> exp(@RequestBody @ApiIgnore SetBean setBean, HttpServletRequest request, HttpServletResponse response)  {
        SetEntity setEntity = new SetEntity();
        LOG.debug("ids:{}" ,setBean.getIds());
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
        String zipDirFileName = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        //临时存放docx的路径
        String path = BasicUtil.getRealPath("") + "/temp/" + zipDirFileName + File.separator;
        File file = new File(path);
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }
        //存放图片的路径
        String imageDir = BasicUtil.getRealPath("") + File.separator + MSProperties.upload.path + File.separator + BasicUtil.getApp().getAppId() + "/cms/content/editor";
        ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) setBiz.excuteSql(exportSql);
        // 遍历生成docx
        for (Map map : results) {
            try {
                if (map.get("docFileContent") instanceof Clob || map.get("docFileContent") instanceof NClobProxyImpl) {
                    Clob content = (Clob) map.get("docFileContent");
                    map.put("docFileContent", StringUtil.clobStr(content));
                }
                //此处将获取的真实路径中 "\" 进行转换，否则会导致替换字符串时 "\" 起转义效果
                String realPath = BasicUtil.getRealPath("").replace("\\","/");
                //图片保存路径存在项目名需要剔除，否则拼接的路径错误
                String contextPath = SpringUtil.getRequest().getContextPath();
                WordUtil.html2Docx(map.get("docFileContent").toString().replaceAll("src=\"" + contextPath, "src=\"" + realPath), path + map.get("docFileName").toString() + ".docx", imageDir);
            } catch (JAXBException e) {
                LOG.error("JAXBException 文章导出错误,文章名称：{}",map.get("docFileName"));
                e.printStackTrace();
            } catch (Docx4JException e) {
                LOG.error("Docx4JException 文章导出错误,文章名称：{}",map.get("docFileName"));
                e.printStackTrace();
            }
        }
        // 压缩docx文件
        File zip = ZipUtil.zip(path);
        // 需要删除临时docx文件
        FileUtil.del(path);

        HttpHeaders headers = new HttpHeaders();
        String zipName = zip.getName();
        headers.add("fileName", zipName);
        headers.setContentDispositionFormData("fileName", zipName);
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(zip);
        } catch (IOException e) {
            LOG.error("读取压缩包错误");
            e.printStackTrace();
        }
        zip.delete();
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }

}
