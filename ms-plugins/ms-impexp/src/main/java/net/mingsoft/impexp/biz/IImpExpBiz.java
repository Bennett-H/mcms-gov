/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.biz;

import net.mingsoft.base.biz.IBaseBiz;
import net.mingsoft.impexp.dao.ISetDao;
import net.mingsoft.impexp.entity.SetEntity;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.http.ResponseEntity;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Map;


/**
 * excel配置业务
 *
 * @author 铭软科技
 * 创建日期：2021-2-2 17:35:57<br/>
 * 历史修订：<br/>
 */
public interface IImpExpBiz extends IBaseBiz<SetEntity> {
    /**
     * 导入excel
     * @param set 导入导出配置实体
     * @param path 文件路径
     * @param paraMap   用于匹配columns中的参数，
     */
    void impExcel(SetEntity set, String path, Map<String,Object> paraMap);

    /**
     * 导入word，核心就是将word文件转html存入数据库
     * 1、通过第三方工具xdocreport与poi
     * 2、word转html
     * 3、读取html的内容
     *  set 中importjson 格式范例
     *   {
     * 		"table":"cms_content",
     * 		"id":"snow",
     * 		"columns":{
     * 				"docFileName":"content_title",
     * 				 "content":"content_details",
     * 				 "categoryId":"category_id" //自定义key value
     *                },
     * 		"defaults":{
     * 				 "content_display":"1"
     *        }
     * }
     *
     *
     * @param set 导入导出配置实体
     * @param docxPath word路径
     * @param imgDir   word中的图片存放路径，
     * @param paraMap   用于匹配columns中的参数，
     * @return 返回html内容
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    boolean impWord(SetEntity set, String docxPath, String imgDir , Map<String,Object> paraMap) throws IOException, TransformerException, ParserConfigurationException;

}
