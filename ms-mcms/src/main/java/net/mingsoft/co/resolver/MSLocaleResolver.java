/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */






package net.mingsoft.co.resolver;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * 自定义国际化语言解析器
 *
 * @author 铭软团队
 * @version 创建日期：2020/12/2 14:27<br/>
 */
public class MSLocaleResolver implements LocaleResolver {

    private static final String I18N_LANGUAGE = "lang";
    private static final String I18N_LANGUAGE_SESSION = "i18n_language_session";

    @Override
    public Locale resolveLocale(HttpServletRequest req) {
        String lang = req.getParameter(I18N_LANGUAGE);
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(lang)) {
            String[] language = lang.split("_");
            locale = new Locale(language[0], language[1]);
            //将国际化语言保存到session
            HttpSession session = req.getSession();
            session.setAttribute(I18N_LANGUAGE_SESSION, locale);
            //修改localDefault
            Configuration configuration = SpringUtil.getBean(Configuration.class);
            try {
                configuration.setSharedVariable("localDefault", locale.toString());
            } catch (TemplateModelException e) {
                e.printStackTrace();
            }
        } else {
            //如果没有带国际化参数，则判断session有没有保存，有保存，则使用保存的，也就是之前设置的，避免之后的请求不带国际化参数造成语言显示不对
            HttpSession session = req.getSession();
            Locale localeInSession = (Locale) session.getAttribute(I18N_LANGUAGE_SESSION);
            if (localeInSession != null) {
                locale = localeInSession;
            } else {
                // session没有保存，默认中文
                locale = Locale.SIMPLIFIED_CHINESE;
            }
        }
        //修改localDefault
        Configuration configuration = SpringUtil.getBean(Configuration.class);
        try {
            configuration.setSharedVariable("localDefault", locale.toString());
        } catch (TemplateModelException e) {
            e.printStackTrace();
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
    }
}
