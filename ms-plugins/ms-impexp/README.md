# 安装步骤


1. 复制 ms-impexp 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-impexp</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-impexp</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-impexp,必须在pom.xml引入的时候需要排除log4j
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-impexp</artifactId>
                <exclusions>
                    <exclusion>
                        <artifactId>logback-classic</artifactId>
                        <groupId>ch.qos.logback</groupId>
                    </exclusion>
                    <exclusion>
                        <artifactId>logback-core</artifactId>
                        <groupId>ch.qos.logback</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json


#参考文档
http://doc.mingsoft.net/plugs/exp-imp/jie-shao.html