# 安装步骤

1. 复制 ms-spider 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
			<!--modules增加模块-->
			<modules>
				<module>ms-spider</module>
			</modules>
			
			<!--dependency增加模块-->
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-spider</artifactId>
				<version>${ms.version}</version>
			</dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-spider
```xml
            <!--必须是第一个依赖的位置-->
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-spider</artifactId>
			</dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json

#参考文档
http://doc.mingsoft.net/plugs/cai-ji-cha-jian/jie-shao.html