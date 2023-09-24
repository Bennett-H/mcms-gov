# 重要说明
必须先安装 kkFileView ，推荐docker安装

# 安装步骤
1. 复制 ms-file 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-file</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-file</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-file
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-file</artifactId>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json

#参考文档
http://doc.mingsoft.net/plugs/wen-jian-guan-li/jie-shao.html