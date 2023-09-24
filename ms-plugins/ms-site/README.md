必须先导入数据库配置与菜单，否则启动报错
# 安装步骤

1. 复制 ms-site 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
			<!--modules增加模块-->
			<modules>
				<module>ms-site</module>
			</modules>
			
			<!--dependency增加模块-->
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-site</artifactId>
				<version>${ms.version}</version>
			</dependency>
```
2. ms-mcms的pom.xml增加依赖 ms-site, 位置必须为首个依赖
```xml
            <!--必须是第一个依赖的位置-->
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-site</artifactId>
			</dependency>
```
3. 更改ShiroConfig :
   注释public ManagerAuthRealm customRealm(SimpleCredentialsMatcher managerLoginCredentialsMatcher)
   打开public BaseAuthRealm customRealm(SimpleCredentialsMatcher managerLoginCredentialsMatcher)
4. 导入doc目录SQL文件
5. 导入doc目录菜单json
6. 在gov的index.ftl的`<head>`标签里引入ms-site-header.ftl: <#include '/site/component/ms-site-header.ftl'/>
7. 添加站点切换组件<ms-site-header></ms-site-header>,通常添加在头部右侧
8. 进入系统,站点管理页面,初始化APPID并 设置对应站点的管理员 ，注意如果是已经存在了很多数据需要 手动设置 对应初始化表的 appid值

# 常见问题
注意导入站群配置的时候注意 mdiy_config是否存在数据库，没有配置数据会出现空指针异常。 这里可以考虑优化自定义配置
