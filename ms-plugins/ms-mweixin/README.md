# 安装步骤
1. 复制 ms-mweixin 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-mweixin</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-mweixin</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-mweixin
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-mweixin</artifactId>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json


#参考文档
http://doc.mingsoft.net/plugs/weixin/jie-shao.html


