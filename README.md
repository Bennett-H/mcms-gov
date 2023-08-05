# 开发环境 
推荐 JDK 1.8、IntelliJ IDEA，数据库如果是MySQL推荐5.7以上版本(需要设置忽略大小写)

# 后台默认访问地址 http://ip:8080/ms/login.do
系统默认超级管理员账号:
## 超级管理员
账号:msopen
密码:Msopen@2012

## 安全管理员
账号:syssso
密码:Msopen@2022

## 审计管理员
账号:sysauditor
密码:Msopen@2022

## 系统管理员
账号:sys
密码:Msopen@2022

# 默认jdk版本 1.8
如果遇到

# 常见问题
这个在线文档能解决大部分开发过程中遇到的问题
http://doc.mingsoft.net/mcms/chang-jian-wen-ti/qi-ye-ban-3001-zheng-wu-ban.html


# 开发要求
1. idea  必须设置 `Working directory` 目录（MSApplication.java所在的目录，这里是ms-mcms)，然后重启项目
   参考 http://doc.mingsoft.net/mcms/chang-jian-wen-ti/qi-ye-ban-3001-zheng-wu-ban.html#%E5%90%8E%E5%8F%B0%E9%A1%B5%E9%9D%A2404%E3%80%81%E6%A8%A1%E6%9D%BF%E7%AE%A1%E7%90%86%E8%AF%BB%E5%8F%96%E4%B8%8D%E5%88%B0%E6%A8%A1%E6%9D%BF%E6%96%87%E4%BB%B6%E3%80%81%E6%A8%A1%E6%9D%BF%E4%B8%8A%E4%BC%A0%E5%88%B0%E9%94%99%E8%AF%AF%E8%B7%AF%E5%BE%84%E7%AD%89
2. es 安装的版本必须是 7.9.0 安装参考  http://doc.mingsoft.net/plugs/zi-dong-sheng-cheng-bian-hao/jie-shao.html
3. 本地制作模版的时候，数据库的数据库少一点，可以避免静态化带来的不好体验，例如：如果本地有10W数据，如果模版还在频繁改动，返回静态化时候需要等待的时间
   就会长一些，所以建议本地开发模版不要用太多的数据，模版制作好后上线统一静态化一次就可以，实际生产中都是自动静态化，不需要手动进行静态化。
4. 对应数据量比较多的时候建议 `列表页面` 采用接口方式来制作，这样可以减少栏目分页的生成 参考处理方式 http://doc.mingsoft.net/mcms/chang-jian-wen-ti/qi-ye-ban-3001-zheng-wu-ban.html#%E6%95%B0%E6%8D%AE%E9%87%8F%E5%A4%A7%E9%9D%99%E6%80%81%E5%8C%96%E6%85%A2
5. 数据库必须启用忽略大小写


# 模块说明
ms-baidu  百度AI模块，用于错词检测
ms-base 基础框架模块，主要管理依赖jar
ms-basic 基础框架模块，主要系统基本功能
ms-datascope 数据权限插件
ms-elasticsearch 全文搜索
ms-ip ip白名单
ms-mattention 关注插件
ms-mcms 内容c插件
ms-mcomment 评论插件
ms-mdiy 自定义插件
ms-mpeople 会员插件
ms-msend 发送插件
ms-progress 进度审批插件
ms-quartz 定时调度插件
ms-wordfilter 敏感词插件

插件相关文档 http://doc.mingsoft.net/plugs/

# 源码部署
参考 http://doc.mingsoft.net/mcms/kai-yuan-ban-ben.html
# 生产部署（推荐jar部署方式）
参考 http://doc.mingsoft.net/server/huan-jing-pei-zhi/jarbu-shu.html
# war部署
参考 http://doc.mingsoft.net/server/huan-jing-pei-zhi/warbu-shu.html
# 开发手册
插件参考 http://doc.mingsoft.net/plugs/ （部分插件需付费获得）
二次开发 http://i.mingsoft.net:8000/java-fast-dev/ （需账号访问）

# 使用手册
https://docs.qq.com/doc/DRHl3VWhURFFOWnpi

# 数据库文件
讨论组提供SQL脚本,数据库字典见 数据字典.pdf 文件

