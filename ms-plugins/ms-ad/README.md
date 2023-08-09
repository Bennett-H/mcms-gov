# 重要说明
如果出现使用标签无法正常显示广告，检查一下浏览器是否安装了广告插件。

# 安装步骤
1. 复制 ms-ad 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
            <!--modules增加模块-->
            <modules>
                <module>ms-ad</module>
            </modules>
            
            <!--dependency增加模块-->
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-ad</artifactId>
                <version>${ms.version}</version>
            </dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-ad
```xml
            
            <dependency>
                <groupId>net.mingsoft</groupId>
                <artifactId>ms-ad</artifactId>
            </dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json

#参考文档
http://doc.mingsoft.net/plugs/guang-gao-cha-jian/jie-shao.html