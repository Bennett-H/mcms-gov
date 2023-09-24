
# 安装步骤
1. 复制 ms-id 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-id</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-id</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-id
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-id</artifactId>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json

#参考文档
http://doc.mingsoft.net/plugs/id-bian-ma-gui-zhe/jie-shao.html#%E7%BC%96%E7%A0%81%E8%A7%84%E5%88%99%E6%8F%92%E4%BB%B6