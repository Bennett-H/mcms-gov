/**
 * 版权所有 © 铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.docx.poi.xhtml.XWPF2XHTMLConverter;
import net.mingsoft.base.constant.Const;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.JsoupUtil;
import org.jsoup.Jsoup;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.*;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * word通用工具,需要依赖poi与docx4j
 * 1、poi的转换处理比较麻烦，稳定性不是很好，例如：word2html 结果再转回word格式有差异
 * 2、docx4j处理比较方便，推荐使用，兼容性好
 * 3、poi与docx4j都存在jar包版本冲突问题，需注意pom.xml
 */
public class WordUtil {

    /**
     * 基于docx4j，html转换word
     * 参考：https://github.com/plutext/docx4j-ImportXHTML/blob/master/src/samples/java/org/docx4j/samples/XhtmlToDocxAndBack.java
     *
     * @param content html内容
     * @param docPath doc生成路径
     * @param imgPath 图片资源路径
     * @throws JAXBException
     * @throws Docx4JException
     */
    public static void html2Docx(String content, String docPath, String imgPath) throws JAXBException, Docx4JException {
        String reg = "<img ([^>]*src=['\"]([^'\"]+)[^>]*)/>";
        StringBuffer operatorStr = new StringBuffer(content);
        Pattern compile = Pattern.compile(reg);
        Matcher matcher = compile.matcher(content);
        while (matcher.find()){
            operatorStr.replace(matcher.end(1),matcher.end(),"></img>");
            matcher = compile.matcher(operatorStr);
        }
        //为保证顶级节点，外层嵌套div
        content = String.format("<div>%s</div>", operatorStr);
        // 未闭合标签会导致解析出错，使用Jsoup处理下未闭合标签
        // 注：目前编辑器正常使用
        org.jsoup.nodes.Document document = Jsoup.parse(content);
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        // 由于Jsoup会解析出完整的html格式，这里直接取body的内容即可
        org.jsoup.nodes.Element element = document.getElementsByTag("body").get(0);
        content = element.html();
        //转移html字符,防止报错
        content = Parser.unescapeEntities(content, true);
        WordprocessingMLPackage docxOut = WordprocessingMLPackage.createPackage();
        NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
        docxOut.getMainDocumentPart().addTargetPart(ndp);
        ndp.unmarshalDefaultNumbering();
        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(docxOut);
        XHTMLImporter.setHyperlinkStyle("Hyperlink");
        docxOut.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(content, imgPath));
        docxOut.save(new java.io.File(docPath));
    }

    /**
     * 基于docx4j，word转换html
     * 参考：https://github.com/plutext/docx4j-ImportXHTML/blob/master/src/samples/java/org/docx4j/samples/DocxToXhtmlAndBack.java
     *
     * @param docPath doc文档路径
     * @param imgPath 图片保存资源路径
     * @return 返回html内容 null:生成临时文件失败
     * @throws Exception
     */
    public static String docx2Html(String docPath, String imgPath) {
        String temp = null;
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(docPath));
            // XHTML export
            AbstractHtmlExporter exporter = new HtmlExporterNG2();
            AbstractHtmlExporter.HtmlSettings htmlSettings = new AbstractHtmlExporter.HtmlSettings();
            htmlSettings.setWmlPackage(wordMLPackage);
            htmlSettings.setImageDirPath(BasicUtil.getRealPath(imgPath));
            htmlSettings.setImageTargetUri(imgPath);
            //处理ul列表标签
            SdtWriter.registerTagHandler("HTML_ELEMENT", new SdtToListSdtTagHandler());
            //生成临时html文件
            temp = BasicUtil.getRealPath("/temp" + File.separator + System.currentTimeMillis() + ".html");

            OutputStream os = new java.io.FileOutputStream(temp);
            Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_NONE);
            //读取html文件
            File html = new File(temp);
            String content = FileUtil.readString(html, Const.UTF8);
            //提取body中间对内容
            String contextPath = SpringUtil.getRequest().getContextPath();
            content = ReUtil.get("<body>([\\s\\S]*?)</body>", content, 1).replaceAll("src=\"", "src=\""+contextPath);
            //content = ReUtil.replaceAll(content, "src=\"", "src=\""+contextPath);
            //读取完成删除文件
            html.delete();//可以注释掉这行进行调试，观察生成的文件
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            if (temp != null) {
                FileUtil.del(temp);
            }

        }
        return null;

    }

    /**
     * 基于poi，word转换html,推荐使用docx4j
     * 导入word，核心就是将word文件转html存入数据库
     * 流程:
     * 1、通过第三方工具xdocreport与poi
     * 2、word转html
     * 3、读取html的内容
     * 临时文件生成再当前项目temp文件夹；
     *
     * @param wordPath  word路径
     * @param uploadDir 保存文件夹的目录存放路径,null资源文件夹采用base64位显示 ，例如：图片最终地址 baseDir/imgDir/文件名称
     * @param imgDir    word中的图片存放路径，当baseDir==null,imgDir参数无效
     * @return 返回html内容
     */
    public static String word2Html(String wordPath, String uploadDir, String imgDir) throws IOException, TransformerException, ParserConfigurationException {
        //word文件
        File file = new File(wordPath);

        //临时生成的文件
        String type = FileTypeUtil.getType(file);
        String temp = new File("").getCanonicalPath() + "/temp" + File.separator + System.currentTimeMillis() + ".html";
        //根据type执行不同的转换方法
        if (type.equalsIgnoreCase("docx")) { //新版本word
            //创建dom对象
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            //配置，通过这个方法可以转换很多种格式，例如ppt,pdf,html,doc等,具体看https://github.com/opensagres/xdocreport
            Options options = Options.getFrom("DOCX");
            //构建html配置
            XHTMLOptions xhtmlOptions = XWPF2XHTMLConverter.getInstance().toXHTMLOptions(options);
            //根据uploadDir决定是否生成图片
            if (StringUtils.isNotEmpty(uploadDir)) {
                //图片最终存放在 uploadDir+imgDir下
                xhtmlOptions.setImageManager(new ImageManager(new File(uploadDir), imgDir));
            } else {
                //base64位，不回生成图片
                xhtmlOptions.setImageManager(new Base64EmbedImgManager());
            }
            //转换文件，这里可以再深究源代码是否可以直接获取html代码
            XHTMLConverter.getInstance().convert(document, new FileOutputStream(temp), xhtmlOptions);

        } else if (type.equalsIgnoreCase("doc")) { //旧版本word
            //创建word dom对象
            HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(file));
            //创建dom对象
            org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            //创建转换对象
            WordToHtmlConverter wordToHtmlConverter = null;
            //根据uploadDir决定是否生成图片
            if (StringUtils.isNotEmpty(uploadDir)) {

                //先创建文件夹，不管文件夹有多少层
                File _file = new File(uploadDir + imgDir);
                if (!_file.exists()) {
                    _file.mkdirs();
                }
                wordToHtmlConverter = new WordToHtmlConverter(document);
                //保存图片，并返回图片的相对路径
                wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
                    try (FileOutputStream out = new FileOutputStream(uploadDir + imgDir + File.separator + name)) {
                        out.write(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return uploadDir + File.separator + imgDir + File.separator + name;
                });
            } else {
                wordToHtmlConverter = new ImageConverter(document);
            }

            wordToHtmlConverter.processDocument(wordDocument);
            org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(new File(temp));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
        }
        //移除文件
        File html = new File(temp);
        if (html.exists()) {
            String content = FileUtil.readString(html, Const.UTF8);
            html.delete(); //可以注释掉这行进行调试，观察生成的文件

            return content;
        } else {
            return null;
        }
    }

    /**
     * 针对老的word进行图片资源处理
     */
    static class ImageConverter extends WordToHtmlConverter {

        public ImageConverter(Document document) {
            super(document);
        }

        @Override
        protected void processImageWithoutPicturesManager(Element currentBlock, boolean inlined, Picture picture) {
            Element imgNode = currentBlock.getOwnerDocument().createElement("img");
            StringBuffer sb = new StringBuffer();
            sb.append(Base64.getMimeEncoder().encodeToString(picture.getRawContent()));
            sb.insert(0, "data:" + picture.getMimeType() + ";base64,");
            imgNode.setAttribute("src", sb.toString());
            currentBlock.appendChild(imgNode);
        }
    }
}
