
# 安装步骤

1. 复制 ms-statistics 到主项目
2. ms-parent 中的 pom.xml 增加依赖
```xml
			<!--modules增加模块-->
			<modules>
				<module>ms-statistics</module>
			</modules>
			
			<!--dependency增加模块-->
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-statistics</artifactId>
				<version>${ms.version}</version>
			</dependency>
```
3. ms-mcms的pom.xml增加依赖 ms-statistics
```xml
			<dependency>
				<groupId>net.mingsoft</groupId>
				<artifactId>ms-statistics</artifactId>
			</dependency>
```

4. 导入doc目录SQL文件
5. 导入doc目录菜单json


#参考文档

http://doc.mingsoft.net/plugs/tong-ji-cha-jian/jie-shao.html