/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package com.jagregory.shiro.freemarke;

import com.jagregory.shiro.freemarker.SecureTag;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Equivalent to {@link org.apache.shiro.web.tags.PermissionTag}</p>
 */
public abstract class PermissionTag extends SecureTag {
    String getName(Map params) {
        return getParam(params, "name");
    }

    @Override
    protected void verifyParameters(Map params) throws TemplateModelException {
        String permission = getName(params);

        if (permission == null || permission.length() == 0) {
            throw new TemplateModelException("The 'name' tag attribute must be set.");
        }
    }

    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        String p = getName(params);

        boolean show = showTagBody(p);
        if (show) {
            renderBody(env, body);
        }
    }

    protected boolean isPermitted(String p) {
        boolean isPermitted = false;
        if(StringUtils.isNotBlank(p) && getSubject() != null) {
            String[] ps = p.split(",");
            for(String _p : ps ) {
                if(getSubject().isPermitted(_p)) {
                    isPermitted = true;
                    break;
                }
            }

        }
        return isPermitted;
    }

    protected abstract boolean showTagBody(String p);
}
