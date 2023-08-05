/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.co.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import net.mingsoft.base.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.co.constant.Const;
import net.mingsoft.mdiy.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.helpers.DefaultHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图片工具类
 * @author 铭软开发团队
 * 创建日期：2023-04-11 09:32<br/>
 * 历史修订：br/>
 */
public class ImageUtil {


    /**
     * 开启水印后，会根据传入的相对路径地址找到文件并添加水印
     * @param filePath 相对路径地址
     * @throws IOException
     */
    public static void imgWatermark(String filePath) throws IOException {
        Map<String, String> configMap = ConfigUtil.getMap("图片水印配置");
        String uploadFolderPath = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadPath", "upload");
        String uploadMapping = ConfigUtil.getString(Const.CONFIG_UPLOAD, "uploadMapping", "/upload/**");
        // 判断是否开启水印
        if (BooleanUtil.toBoolean(configMap.get("watermarkEnable"))){
            //去掉映射路径
            filePath = filePath.replace(uploadMapping.replace("**",""),"");
            File file = null;
            if(FileUtil.isAbsolutePath(uploadFolderPath)) {
                //源图片
                file = FileUtil.file(uploadFolderPath+"/"+filePath);
            } else {
                file = FileUtil.file(BasicUtil.getRealPath(uploadFolderPath + File.separator +filePath));
            }
            // 文件不存在或者非图片类型则手动抛出异常
            if (!file.exists() || !isImage(file)) {
                throw new BusinessException("不合法的文件，文件并不存在或者不是图片类型，可能使用了云存储");
            }
            //缩放比例
            float verticalScale = Float.valueOf(configMap.get("verticalScale"));
            float lateralScale = Float.valueOf(configMap.get("lateralScale"));
            BufferedImage bufferedImage = ImageIO.read(file);
            //计算水印缩放后的宽高
            int height = (int) (bufferedImage.getHeight() * verticalScale);// 纵向比例
            int width = (int) (bufferedImage.getWidth() * lateralScale);// 横向比例

            //水印图片
            BufferedImage pass = null;
            //水印类型
            String watermarkType = configMap.get("watermarkType");
            //图片类型
            if (WatermarkType.IMAGE.equalsIgnoreCase(watermarkType)) {
                String imageWatermark = configMap.get("imageWatermark");
                List<Map> list = JSONUtil.toList(imageWatermark,Map.class);
                if (CollUtil.isEmpty(list)){
                    //图片为空
                    throw new BusinessException("水印图片为空");
                }
                String imageWatermarkPath = BasicUtil.getRealPath(list.get(0).get("path").toString());
                pass = ImgUtil.read(FileUtil.file(imageWatermarkPath));

            } else if (WatermarkType.TEXT.equalsIgnoreCase(watermarkType)) {//文字类型
                //内容
                String imageWatermark = configMap.get("watermarkText");
                //字体
                String font = configMap.get("font");
                //颜色
                String fontColor = configMap.get("fontColor");

                if (StringUtils.isBlank(imageWatermark) || StringUtils.isBlank(fontColor) || StringUtils.isBlank(font)) {
                    //文字信息为空
                    throw new BusinessException("");
                }

                pass = ImgUtil.createImage(imageWatermark,
                        new Font(font, Font.PLAIN, (int) (height * 0.75)),
                        null,
                        Color.decode(fontColor),
                        BufferedImage.TYPE_INT_ARGB);
            } else {
                //空类型或未知
                throw new BusinessException("");
            }
            if(configMap.get("verticalScale") == null || configMap.get("lateralScale") == null){
                //无缩放比例
                throw new BusinessException("没有对应缩放比例");
            }

            //缩放水印
            pass = ImgUtil.toBufferedImage(ImgUtil.scale(pass, width,height));

            //水印位置
            String watermarkPosition = configMap.get("watermarkPosition");
            Rectangle rectangle = calculationPosition(file, pass,watermarkPosition);


            //透明度
            String alpha = configMap.get("alpha");
            if (StringUtils.isBlank(alpha)){
                //透明度为空
                throw new BusinessException("");
            }

            //判断是否是图片文件
            if (isImage(file)) {
                //添加水印
                ImgUtil.pressImage(
                        file,
                        file,
                        pass, //水印图片
                        rectangle.x,
                        rectangle.y,//y坐标修正值。 默认在中间，偏移量相对于中间偏移
                        Float.valueOf(alpha)
                );
            }
        }
    }

    /** 计算水印的位置
     * @param img
     * @param pass
     * @param position
     * @return
     * @throws IOException
     */
    private static Rectangle calculationPosition(File img, BufferedImage pass, String position) throws IOException {
        FileInputStream imgInputStream = null;
        try {
            imgInputStream = new FileInputStream(img);
            //图片的宽高
            BufferedImage bufferedImage = ImageIO.read(imgInputStream);
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();

            //水印的宽高
            int pressImgHeight = pass.getHeight();
            int pressImgWidth = pass.getWidth();

            Rectangle rectangle = new Rectangle();
            //默认是0
            rectangle.y = 0;
            rectangle.x = 0;

            switch (position){
                case PositionType.UPPER_LEFT:
                    rectangle.y = -(height - pressImgHeight) / 2;
                    rectangle.x = -(width - pressImgWidth) / 2;
                    break;
                case PositionType.LOWER_LEFT:
                    rectangle.y = (height - pressImgHeight) / 2;
                    rectangle.x = -(width - pressImgWidth) / 2;
                    break;
                case PositionType.UPPER_RIGHT:
                    rectangle.y = -(height - pressImgHeight) / 2;
                    rectangle.x = (width - pressImgWidth) / 2;
                    break;
                case PositionType.LOWER_RIGHT:
                    rectangle.y = (height - pressImgHeight) / 2;
                    rectangle.x = (width - pressImgWidth) / 2;
                    break;
                case PositionType.MIDDLE:
                    rectangle.y = 0;
                    rectangle.x = 0;
                    break;
            }

            return rectangle;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(imgInputStream);
        }
        return null;

    }


    /**
     * 判断是否是图片
     *
     * @param file 文件
     * @return boolean
     */
    public static boolean isImage(File file) {
        String type = getMimeType(file);
        Pattern p = Pattern.compile("image/.*");
        Matcher m = p.matcher(type);
        return m.matches();
    }

    /**
     * 获取类型
     *
     * @param file 文件
     * @return String
     */
    public static String getMimeType(File file) {
        if (file.isDirectory()) {
            return "the target is a directory";
        }

        AutoDetectParser parser = new AutoDetectParser();
        parser.setParsers(new HashMap<>());
        Metadata metadata = new Metadata();
        try (InputStream stream = new FileInputStream(file)) {
            parser.parse(stream, new DefaultHandler(), metadata, new ParseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }


    /**
     * 水印类型
     */
    public static class WatermarkType {

        /**
         * 图片
         */
        public static final String IMAGE = "image";

        /**
         * 文字
         */
        public static final String TEXT = "text";

    }

    /**
     * 水印位置
     */
    public static class PositionType {

        /**
         * 左上
         */
        public static final String UPPER_LEFT = "upperLeft";

        /**
         * 左下
         */
        public static final String LOWER_LEFT = "lowerLeft";

        /**
         * 右上
         */
        public static final String UPPER_RIGHT = "upperRight";

        /**
         * 右下
         */
        public static final String LOWER_RIGHT = "lowerRight";

        /**
         * 中间
         */
        public static final String MIDDLE = "middle";


    }
}
