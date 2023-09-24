# 安装步骤
1. 复制 ms-qa 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-qa</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-qa</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-qa
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-qa</artifactId>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json

#参考文档
http://doc.mingsoft.net/plugs/qa/jie-shao.html
