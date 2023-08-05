/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */

package net.mingsoft.config;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Aspect
@EnableAspectJAutoProxy
@Component
public class SwaggerSuffixAspect {

	@AfterReturning(pointcut = "execution(public io.swagger.models.Swagger springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl.mapDocumentation(..))", returning = "swagger")
	public void doBeforeBussinessCheck(Swagger swagger) {
		Map<String, Path> paths = swagger.getPaths();
		if (null != paths) {
			Map<String, Path> newPaths = new HashMap<String, Path>(paths);
			paths.clear();
			Iterator<String> it = newPaths.keySet().iterator();
			while (it.hasNext()) {
				String oldKey = it.next();
				String newKey = oldKey + ".do";
				paths.put(newKey, newPaths.get(oldKey));
			}
			newPaths = null;
		}
	}
}
