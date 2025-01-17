/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */










package net.mingsoft.basic.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.mingsoft.base.constant.e.BaseCookieEnum;
import net.mingsoft.base.constant.e.BaseEnum;
import net.mingsoft.base.constant.e.BaseSessionEnum;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.constant.Const;
import net.mingsoft.basic.entity.AppEntity;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.config.MSProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @ClassName: BasicUtil
 * @Description: TODO(业务工具类)
 * @author 铭软开发团队
 * @date 2020年7月2日
 * 历史修订: 2022/1/17 getClassPath() 防止服务器启动和本地启动的路径不同
 * 			2022/5/9  getMimeType() 获取文件真实类型
 */
public class BasicUtil  {

	protected static final Logger LOG = LoggerFactory.getLogger(BasicUtil.class);
	/** Wap网关Via头信息中特有的描述信息 */
	private static String mobileGateWayHeaders[] = new String[] { "ZXWAP", // 中兴提供的wap网关的via信息，例如：Via=ZXWAP
			// GateWayZTE
			// Technologies，
			"chinamobile.com", // 中国移动的诺基亚wap网关，例如：Via=WTP/1.1
			// GDSZ-PB-GW003-WAP07.gd.chinamobile.com (Nokia
			// WAP Gateway 4.1 CD1/ECD13_D/4.1.04)
			"monternet.com", // 移动梦网的网关，例如：Via=WTP/1.1
			// BJBJ-PS-WAP1-GW08.bj1.monternet.com. (Nokia
			// WAP
			// Gateway 4.1 CD1/ECD13_E/4.1.05)
			"infoX", // 华为提供的wap网关，例如：Via=HTTP/1.1 GDGZ-PS-GW011-WAP2
			// (infoX-WISG
			// Huawei Technologies)，或Via=infoX WAP Gateway V300R001
			// Huawei Technologies
			"XMS 724Solutions HTG", // 国外电信运营商的wap网关，不知道是哪一家
			"wap.lizongbo.com", // 自己测试时模拟的头信息
			"Bytemobile",// 貌似是一个给移动互联网提供解决方案提高网络运行效率的，例如：Via=1.1 Bytemobile OSN
			// WebProxy/5.1
	};
	/** 电脑上的IE或Firefox浏览器等的User-Agent关键词 */
	private static String[] pcHeaders = new String[] { "Windows 98", "Windows ME", "Windows 2000", "Windows XP",
			"Windows NT", "Ubuntu" };
	/** 手机浏览器的User-Agent里的关键词 */
	private static String[] mobileUserAgents = new String[] { "Nokia", // 诺基亚，有山寨机也写这个的，总还算是手机，Mozilla/5.0
			// (Nokia5800
			// XpressMusic)UC
			// AppleWebkit(like
			// Gecko)
			// Safari/530
			"SAMSUNG", // 三星手机
			// SAMSUNG-GT-B7722/1.0+SHP/VPP/R5+Dolfin/1.5+Nextreaming+SMM-MMS/1.2.0+profile/MIDP-2.1+configuration/CLDC-1.1
			"MIDP-2", // j2me2.0，Mozilla/5.0 (SymbianOS/9.3; U; Series60/3.2
			// NokiaE75-1 /110.48.125 Profile/MIDP-2.1
			// Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML like
			// Gecko) Safari/413
			"CLDC1.1", // M600/MIDP2.0/CLDC1.1/Screen-240X320
			"SymbianOS", // 塞班系统的，
			"MAUI", // MTK山寨机默认ua
			"UNTRUSTED/1.0", // 疑似山寨机的ua，基本可以确定还是手机
			"Windows CE", // Windows CE，Mozilla/4.0 (compatible; MSIE 6.0;
			// Windows CE; IEMobile 7.11)
			"iPhone", // iPhone是否也转wap？不管它，先区分出来再说。Mozilla/5.0 (iPhone; U; CPU
			// iPhone OS 4_1 like Mac OS X; zh-cn) AppleWebKit/532.9
			// (KHTML like Gecko) Mobile/8B117
			"iPad", // iPad的ua，Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X;
			// zh-cn) AppleWebKit/531.21.10 (KHTML like Gecko)
			// Version/4.0.4 Mobile/7B367 Safari/531.21.10
			"Android", // Android是否也转wap？Mozilla/5.0 (Linux; U; Android
			// 2.1-update1; zh-cn; XT800 Build/TITA_M2_16.22.7)
			// AppleWebKit/530.17 (KHTML like Gecko) Version/4.0
			// Mobile Safari/530.17
			"BlackBerry", // BlackBerry8310/2.7.0.106-4.5.0.182
			"UCWEB", // ucweb是否只给wap页面？ Nokia5800
			// XpressMusic/UCWEB7.5.0.66/50/999
			"ucweb", // 小写的ucweb貌似是uc的代理服务器Mozilla/6.0 (compatible; MSIE 6.0;)
			// Opera ucweb-squid
			"BREW", // 很奇怪的ua，例如：REW-Applet/0x20068888 (BREW/3.1.5.20; DeviceId:
			// 40105; Lang: zhcn) ucweb-squid
			"J2ME", // 很奇怪的ua，只有J2ME四个字母
			"YULONG", // 宇龙手机，YULONG-CoolpadN68/10.14 IPANEL/2.0 CTC/1.0
			"YuLong", // 还是宇龙
			"COOLPAD", // 宇龙酷派YL-COOLPADS100/08.10.S100 POLARIS/2.9 CTC/1.0
			"TIANYU", // 天语手机TIANYU-KTOUCH/V209/MIDP2.0/CLDC1.1/Screen-240X320
			"TY-", // 天语，TY-F6229/701116_6215_V0230 JUPITOR/2.2 CTC/1.0
			"K-Touch", // 还是天语K-Touch_N2200_CMCC/TBG110022_1223_V0801 MTK/6223
			// Release/30.07.2008 Browser/WAP2.0
			"Haier", // 海尔手机，Haier-HG-M217_CMCC/3.0 Release/12.1.2007
			// Browser/WAP2.0
			"DOPOD", // 多普达手机
			"Lenovo", // 联想手机，Lenovo-P650WG/S100 LMP/LML Release/2010.02.22
			// Profile/MIDP2.0 Configuration/CLDC1.1
			"LENOVO", // 联想手机，比如：LENOVO-P780/176A
			"HUAQIN", // 华勤手机
			"AIGO-", // 爱国者居然也出过手机，AIGO-800C/2.04 TMSS-BROWSER/1.0.0 CTC/1.0
			"CTC/1.0", // 含义不明
			"CTC/2.0", // 含义不明
			"CMCC", // 移动定制手机，K-Touch_N2200_CMCC/TBG110022_1223_V0801 MTK/6223
			// Release/30.07.2008 Browser/WAP2.0
			"DAXIAN", // 大显手机DAXIAN X180 UP.Browser/6.2.3.2(GUI) MMP/2.0
			"MOT-", // 摩托罗拉，MOT-MOTOROKRE6/1.0 LinuxOS/2.4.20 Release/8.4.2006
			// Browser/Opera8.00 Profile/MIDP2.0 Configuration/CLDC1.1
			// Software/R533_G_11.10.54R
			"SonyEricsson", // 索爱手机，SonyEricssonP990i/R100 Mozilla/4.0
			// (compatible; MSIE 6.0; Symbian OS; 405) Opera
			// 8.65 [zh-CN]
			"GIONEE", // 金立手机
			"HTC", // HTC手机
			"ZTE", // 中兴手机，ZTE-A211/P109A2V1.0.0/WAP2.0 Profile
			"HUAWEI", // 华为手机，
			"webOS", // palm手机，Mozilla/5.0 (webOS/1.4.5; U; zh-CN)
			// AppleWebKit/532.2 (KHTML like Gecko) Version/1.0
			// Safari/532.2 Pre/1.0
			"GoBrowser", // 3g GoBrowser.User-Agent=Nokia5230/GoBrowser/2.0.290
			// Safari
			"IEMobile", // Windows CE手机自带浏览器，
			"WAP2.0"// 支持wap 2.0的
	};



	/**
	 * 获取当前模块对应的app
	 * 必返回一条
	 * 注意：没有站群插件appUrl会设置当前域名
	 */
	public static AppEntity getApp() {

		AppEntity app = BasicUtil.getWebsiteApp();

		//如果有站群插件，且用户访问的地址是有效的
		if (app == null) {
			AppEntity _app = null;
			IAppBiz appBiz = SpringUtil.getBean(IAppBiz.class);

			//尝试从 request 对象获取，  非静态化多线程下 request 不为空
			if(SpringUtil.getRequest() != null){

				if (MapCacheUtil.get(BasicUtil.getDomain()) != null){
					_app = (AppEntity) MapCacheUtil.get(BasicUtil.getDomain());
				}else {
					_app = appBiz.getOne(
							Wrappers.<AppEntity>lambdaQuery()
									.like(AppEntity::getAppUrl, BasicUtil.getDomain()), false);
					if (_app == null){
						MapCacheUtil.put(BasicUtil.getDomain(),new AppEntity());

					}else {
						MapCacheUtil.put(BasicUtil.getDomain(),_app);
					}
				}
			}

			//多线程下_app为null因为无法获取到request对象
			if (_app == null || StringUtils.isBlank(_app.getId())) {
				if (MapCacheUtil.get("firstApp") != null){
					_app = (AppEntity) MapCacheUtil.get("firstApp");
				}else {
					_app = appBiz.getFirstApp();
					MapCacheUtil.put("firstApp",_app);
				}
			}

			//开源： 没有站群插件appUrl会设置当前域名
			if (_app != null) {
				//需要先判断当前是request请求，如果不是就默认app表的地址
				if (SpringUtil.getRequest() != null) {
					_app.setAppUrl(BasicUtil.getUrl());
				}

				return _app;
			}
		}
		return app;
	}

	/**
	 * 获取当前模块对应的app
	 * 使用了站群插件才会有值
	 */

	public static AppEntity getWebsiteApp() {
		return (AppEntity)getSession(Const.APP);
	}
	/**
	 * 获取上下文路径
	 * @return
	 */
	public static String getContextPath() {
		Environment environment = SpringUtil.getBean(Environment.class);
		return environment.getProperty("server.servlet.context-path", String.class, "/");
	}
	/**
	 * 获取项目路径
	 * @param server 指定servier主机
	 * @return 返回项目路径，例如：http://www.ip.com/projectName 后面没有反斜杠，后面接地址或参数必须加/
	 */
	public static String getUrl(String server) {
		HttpServletRequest request = SpringUtil.getRequest();
		//从线程变量中获取appId，若为Null则按照原来的规则获取
		if (BasicUtil.getWebsiteApp() != null) {
			LOG.debug("BasicUtil.getWebsiteApp() != null is true");
			return BasicUtil.getWebsiteApp().getAppUrl();
		}
		Environment environment = SpringUtil.getBean(Environment.class);
		String scheme = environment.getProperty("ms.scheme", String.class, "http");
		int port = environment.getProperty("server.port", Integer.class, 8080);
		String path = environment.getProperty("server.servlet.context-path", String.class, "/");
		if(request == null) {
			return null;
		}
		String serverName = request.getServerName();
		//域名访问就直接赋值
		if(!StringUtil.isIP(serverName)) {
			server = serverName;
		}

		String basePath = scheme + "://" + Optional.ofNullable(server).orElse(request.getServerName());
		//域名访问时候就不需要取配置的端口
		int _port = request.getServerPort();
		if(_port==80 || _port != port) {
			port = _port;
		}

		// 80 与 443端口可以不用拼接端口
		if (port == 80 || port == 443) {
			if(!"/".equals(path)) {
				basePath += path + "/";
			} else {
				basePath += "/";
			}
		} else {
			basePath += ":" + port;
			if(!"/".equals(path)) {
				basePath += path + "/";
			} else {
				basePath += "/";
			}
		}

		LOG.debug("server:{},basePath:{}",server,basePath);
		return basePath;
	}

	/**
	 * 获取项目路径
	 *
	 * @return 返回项目路径，例如：http://www.ip.com/projectName 后面没有反斜杠，后面接地址或参数必须加/
	 */
	public static String getUrl() {
		return BasicUtil.getUrl(null);
	}

	/**
	 * 获取对应ip地址的mac地址
	 *
	 * @param ip
	 * @return mac地址
	 */
	public String getMACAddress(String ip) {
		String str = "";
		String macAddress = "";
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("MAC Address") > 1) {
						macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return macAddress;
	}

	/**
	 * 获取请求客户端ip
	 *
	 * @return ip地址
	 */
	public static String getIp() {
		HttpServletRequest request = SpringUtil.getRequest();
		String ipAddress = null;
		// ipAddress = this.getRequest().getRemoteAddr();
		LOG.debug("headers:{}",JSONUtil.toJsonStr(request.getHeaderNames()));
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}

		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return "0:0:0:0:0:0:0:1".equals(ipAddress) ? "127.0.0.1" : ipAddress;
	}

	/**
	 * 获取请求域名，域名不包括http请求协议头
	 *
	 * @return 返回域名地址
	 */
	public static String getDomain() {
		HttpServletRequest request = SpringUtil.getRequest();
		String path = request.getContextPath();
		String name = request.getServerName();
		StringBuilder serverName=new StringBuilder();
		serverName.append(name);
		if (request.getServerPort() == 80) {
			serverName.append(path);
		} else {
			serverName.append(":").append(request.getServerPort()).append(path);
		}
		String currentDomain = serverName.toString();
		return currentDomain;

	}

	/**
	 * 默认页码参数
	 */
	@Deprecated
	private final static String PAGE_NO = "pageNo";

	/**
	 * 页码
	 */
	private final static String PAGE_NUMBER = "pageNumber";

	/**
	 * 默认一页显示数量
	 */
	private final static String PAGE_SIZE = "pageSize";
	private final static String PAGE = "page";

	/**
	 * 分页开始方法，必须配合BasicUtil.endPage()一起使用<br/>
	 * 此方法必须紧跟需要分页的查询语句，不能穿插其他sql否则会消耗掉分页次数导致异常。
	 */
	public static void startPage() {
		int pageNo = BasicUtil.getInt(PAGE_NO, 1);
		int pageNumber = BasicUtil.getInt(PAGE_NUMBER, 1);
		int _pageNo = pageNo;
		if (pageNumber > pageNo) {
			_pageNo = pageNumber;
		}
		PageHelper.startPage(_pageNo, BasicUtil.getInt(PAGE_SIZE, 10));
	}

	/**
	 * 每页显示数量
	 *
	 * @return 默认每页10条
	 */
	public static int getPageSize() {
		return BasicUtil.getInt(PAGE_SIZE, 10);
	}

	/**
	 * 当前页码
	 *
	 * @return 默认1
	 */
	public static int getPageNo() {
		return BasicUtil.getInt(PAGE_NO, 1);
	}

	/**
	 * 分页开始方法，必须配合BasicUtil.endPage()一起使用
	 *
	 * @param count
	 *            是否统计总数 如果不需要分页使用false
	 */
	public static void startPage(boolean count) {
		BasicUtil.startPage(BasicUtil.getInt(PAGE_NO, 1), BasicUtil.getInt(PAGE_SIZE, 10), count);
	}

	/**
	 * 分页开始方法，必须配合BasicUtil.endPage()一起使用
	 *
	 * @param count
	 *            是否统计总数 如果不需要分页使用false
	 * @param pageNo
	 *            当前页码
	 * @param pageSize
	 *            一页显示数量
	 */
	public static void startPage(int pageNo, int pageSize, boolean count) {
		PageHelper.startPage(BasicUtil.getInt(PAGE_NO, pageNo), BasicUtil.getInt(PAGE_SIZE, pageSize), count);
	}

	/**
	 * 列表排序
	 *
	 * @param orderBy
	 *            排序字段，多个之间用,隔开
	 * @param order
	 *            默认DESC
	 */
	public static void orderBy(String orderBy, String order) {
		if (!order.equalsIgnoreCase("DESC") && !order.equalsIgnoreCase("ASC")) {
			order = "DESC";
		}
		PageHelper.orderBy(orderBy + " " + order);
	}

	@SuppressWarnings("rawtypes")
	public static PageInfo endPage(List list, String name) {
		@SuppressWarnings("unchecked")
		PageInfo page = new PageInfo(list);
		SpringUtil.getRequest().setAttribute(Const.PARAMS,
				BasicUtil.assemblyRequestUrlParams(new String[] { PAGE_NO }));
		SpringUtil.getRequest().setAttribute(name, page);
		return page;
	}

	@SuppressWarnings("rawtypes")
	public static PageInfo endPage(List list) {
		return BasicUtil.endPage(list, PAGE);
	}
	/**
	 * 获取布尔值
	 *
	 *            HttpServletRequest对象
	 * @param param
	 *            参数名称
	 * @return 返回布尔值，没找到返回null
	 */
	public static Boolean getBoolean(String param) {
		String value = SpringUtil.getRequest().getParameter(param);
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取布尔值
	 *
	 *            HttpServletRequest对象
	 * @param param
	 *            参数名称
	 * @param def
	 *            默认值，如果参数不存在或不符合规则就用默认值替代
	 * @return 返回布尔值，没找到返回默认值
	 */
	public static Boolean getBoolean(String param, Boolean def) {
		String value = SpringUtil.getRequest().getParameter(param);
		if (StringUtils.isBlank(value)) {
			return def;
		}
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 获取整型值
	 *
	 *            HttpServletRequest对象
	 * @param param
	 *            参数名称
	 * @param def
	 *            默认值，如果参数不存在或不符合规则就用默认值替代
	 * @return 返回整型值
	 */
	public static Integer getInt(String param, int def) {
		String value = SpringUtil.getRequest().getParameter(param);
		if (NumberUtils.isNumber(value)) {
			return Integer.parseInt(value);
		} else {
			return def;
		}
	}

	public static Integer getInt(String param) {
		return BasicUtil.getInt(param, 0);
	}

	/**
	 * 获取字符串值
	 *
	 *            HttpServletRequest对象
	 * @param param
	 *            参数名称
	 * @param def
	 *            默认值，如果参数不存在或不符合规则就用默认值替代
	 * @return 返回整型值
	 */
	public static String getString(String param, String def) {
		String value = SpringUtil.getRequest().getParameter(param);
		if (StringUtils.isEmpty(value)) {
			value = def;
		}
		return value;
	}

	/**
	 * 获取字符串值
	 *
	 *            HttpServletRequest对象
	 * @param param
	 *            参数名称
	 * @return 返回整型值
	 */
	public static String getString(String param) {
		return BasicUtil.getString(param, "");
	}

	/**
	 * 获取整型值数组
	 *
	 * @param param
	 *            参数名称
	 * @return 不存在返回null
	 */
	public static int[] getInts(String param) {
		String[] value = SpringUtil.getRequest().getParameterValues(param);
		if (ArrayUtils.isNotEmpty(value)) {
			try {
				return StringUtil.stringsToInts(value);
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 获取整型值数组
	 *
	 * @param param
	 *            参数名称，如果param参数的值本身就是数组，将会忽略split参数
	 * @param split
	 *            分割符号
	 * @return 不存在返回null
	 */
	public static int[] getInts(String param, String split) {
		if (BasicUtil.getInts(param) != null) {
			return BasicUtil.getInts(param);
		}
		String value = SpringUtil.getRequest().getParameter(param);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (ArrayUtils.isNotEmpty(value.split(split))) {
			return StringUtil.stringsToInts(value.split(split));
		} else {
			return null;
		}
	}

	private final static String IDS = "ids";

	public static int[] getIds() {
		return BasicUtil.getInts(IDS);
	}

	/**
	 * 将请求的request的参数重新组装。主要是将空值的替换成null,因为requestMap空值是"",这样处理有利于外部判断,
	 * 同时将获取到的值映射到页面上
	 *
	 *            HttpServletRequest对象
	 * @return 返回处理过后的数据
	 */
	public static Map<String, Object> assemblyRequestMap() {
		HttpServletRequest request = SpringUtil.getRequest();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String[]> map = request.getParameterMap();
		Iterator<String> key = map.keySet().iterator();
		while (key.hasNext()) {
			String k = (String) key.next().replace(".", "_");
			String[] value = null;
			if(!StringUtil.isBlank(map.get(k))) {
				try {
					value = map.get(k);
				} catch (ClassCastException e) {
					value = new String[]{map.get(k)+""};
				}
			}


			if (value == null) {
				params.put(k, null);
				request.setAttribute(k, null);
			} else if (value.length == 1) {
				String temp = null;
				if (!StringUtils.isEmpty(value[0])) {
					temp = value[0];
				}
				params.put(k, temp);
				request.setAttribute(k, temp);
			} else if (value.length == 0) {
				params.put(k, null);
				request.setAttribute(k, null);
			} else if (value.length > 1) {
				params.put(k, value);
				request.setAttribute(k, value);
			}
		}
		return params;
	}

	/**
	 * 将请求的request的参数重新组装。主要是将空值的替换成null,因为requestMap空值是"",这样处理有利于外部判断,
	 * 同时将获取到的值映射到页面上
	 *
	 *            HttpServletRequest对象
	 * @return 返回处理过后的数据
	 */
	public static String assemblyRequestUrlParams() {
		return assemblyRequestUrlParams(null);
	}

	/**
	 * 将请求的request的参数重新组装。主要是将空值的替换成null,因为requestMap空值是"",这样处理有利于外部判断,
	 * 同时将获取到的值映射到页面上
	 *
	 * @param filter
	 *            需要过滤的字段
	 * @return 返回处理过后的数据
	 */

	public static String assemblyRequestUrlParams(String[] filter) {
		Map<String, String[]> map = SpringUtil.getRequest().getParameterMap();
		Iterator<String> key = map.keySet().iterator();
		StringBuffer sb = new StringBuffer();

		while (key.hasNext()) {

			String k = (String) key.next();

			if (filter != null && Arrays.asList(filter).contains(k)) {
				continue;
			}
			if(!StringUtil.isBlank(map.get(k))) {
				String[] value = null;
				try {
					value = map.get(k);
				} catch (ClassCastException e) {
					value = new String[]{map.get(k)+""};
				}

				if (value.length == 1) {
					String temp = "";
					if (!StringUtils.isEmpty(value[0])) {
						temp = value[0];
					}
					sb.append(k).append("=").append(temp).append("&");
				} else if (value.length > 1) {
					sb.append(k).append("=").append(value).append("&");
				}
			}

		}
		if (sb.length() > 0) {

			return sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 移除url参数
	 *
	 * @param fitlers
	 *            需要移除的字段名称
	 */
	public static void removeUrlParams(String[] fitlers) {
		SpringUtil.getRequest().setAttribute(Const.PARAMS, BasicUtil.assemblyRequestUrlParams(fitlers));
	}

	/**
	 * 获取session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 * @return session中获取的对象
	 */
	public static Object getSession(BaseSessionEnum key) {
		return SpringUtil.getRequest().getSession().getAttribute(key.toString());
	}

	/**
	 * 获取session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 * @return session中获取的对象
	 */
	public static Object getSession(String key) {
		if(SpringUtil.getRequest()==null) {
			return null;
		}
		return SpringUtil.getRequest().getSession().getAttribute(key);
	}

	/**
	 * 设置session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 */
	public static void setSession(BaseSessionEnum key, Object value) {
		SpringUtil.getRequest().getSession().setAttribute(key.toString(), value);
	}

	/**
	 * 设置session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 */
	public static void setSession(String key, Object value) {
		SpringUtil.getRequest().getSession().setAttribute(key, value);
	}
	/**
	 * 移除session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 */
	public static void removeSession(BaseSessionEnum key) {
		SpringUtil.getRequest().getSession().removeAttribute(key.toString());
	}

	/**
	 * 移除session的值
	 *
	 *            HttpServletRequest对象
	 * @param key
	 *            枚举类中的值
	 */
	public static void removeSession(String key) {
		SpringUtil.getRequest().getSession().removeAttribute(key);
	}

	/**
	 * 获取filePath的物理路径
	 * 文件分隔符应该使用 File.separator 或 / 避免在linux下无法识别路径
	 * @param filePath
	 *            文件
	 * @return 物理路径
	 */
	public static String getRealPath(String filePath) {
		//非法路径过滤
		if(filePath!=null&&(filePath.contains("../")||filePath.contains("..\\"))){
			throw new BusinessException("非法路径!");
		}
		LOG.debug("filePath {}",filePath);
		String classPath = getClassPath(filePath);
		LOG.debug("classPath {}",classPath);
		if(!classPath.startsWith("file")){
			//Tomcat部署方式和开发
			ServletContext servlet = SpringUtil.getBean(ServletContext.class);
			LOG.debug("servlet {}",servlet);
			String path = servlet.getRealPath(File.separator);
			LOG.debug("path {}",path);
			if(!StringUtils.isEmpty(filePath)){
				String last = path.charAt(path.length() - 1) + ""; // 取最后一个字符串，判断是否存在斜杠
				String frist = filePath.charAt(0) + ""; // 取第一个字符串，判断用户传递过来的参数是否有斜杠
				if (last.equals(File.separator)) {
					if (frist.equals("\\") || frist.equals("/")) {// 当前平台可以获取到最后的斜杠，那么就判断一下用户传递的参数是否有斜杠，有就截取掉
						path = path + filePath.substring(1);
					} else {
						path = path + filePath; // 当前平台可以获取到最后的斜杠，用户传递的参数没有斜杠，就直接返回
					}
				} else {
					if (frist.equals("\\") || frist.equals("/")) {// 当前平台没有斜杠，用户传递的参数有斜杠，有就放回
						path = path + filePath;
					} else {
						path = path + File.separator + filePath; // 平台也米有斜杠，参数也没有斜杠，增加拼接放回
					}
				}
			}
			return path;
		}else {
			//打包为jar包的时候
			String path = System.getProperty("user.dir");
			if (StringUtils.isNotBlank(filePath)){
				path += File.separator+ filePath;
			}
			return path;
		}
	}
	/**
	 * 获取filePath的物理路径
	 *
	 * @param filePath           文件
	 * @return 物理路径
	 */
	public static String getRealTemplatePath(String filePath) {
		String path = null;
		String uploadTemplatePath = MSProperties.upload.template;
		if (new File(uploadTemplatePath).isAbsolute()) {
			String os = System.getProperty("os.name");
			String last = uploadTemplatePath.charAt(uploadTemplatePath.length() - 1) + ""; // 取最后一个字符串，判断是否存在斜杠
			String frist = filePath.charAt(0) + "";
			if(!"\\".equals(frist) || !"/".equals(frist)){
				if (!"\\".equals(last) || !"/".equals(last)) {
					uploadTemplatePath = uploadTemplatePath+"/";
				}
			}else {
				if ("\\".equals(last) || "/".equals(last)) {
					uploadTemplatePath = uploadTemplatePath.substring(0,uploadTemplatePath.length()-1);
				}
			}
			path = uploadTemplatePath + filePath;
			if (os.toLowerCase().startsWith("win")) {
				return path.replace("/", "\\");
			}
		} else {
			path=getRealPath(filePath);
		}
		return path;
	}
	/**
	 * 获取spingboot 对应下的资源文件
	 * @param filePath
	 * @return
	 */
	public static String getClassPath(String filePath) {
		String os = System.getProperty("os.name");
		String temp = null;
		try {
			//temp
			temp = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(os.toLowerCase().startsWith("win")){
			return temp.replace("/", "\\");
		} else {
			return temp;
		}


	}

	/**
	 * 获取对象所有字段 包括父类
	 * @param object
	 * @return
	 */
	public static Field[] getAllFields(Object object){
		Class clazz = object.getClass();
		List<Field> fieldList = new ArrayList<>();
		while (clazz != null){
			fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
			clazz = clazz.getSuperclass();
		}
		Field[] fields = new Field[fieldList.size()];
		fieldList.toArray(fields);
		return fields;
	}
	/**
	 * 通过反射给对象的指定字段赋值
	 *
	 * @param target    目标对象
	 * @param fieldName 字段的名称
	 * @param value     值
	 */
	public static void setValue(Object target, String fieldName, Object value) {
		Class<?> clazz = target.getClass();
		String[] fs = fieldName.split("\\.");
		try {
			for (int i = 0; i < fs.length - 1; i++) {
				Field f = clazz.getDeclaredField(fs[i]);
				f.setAccessible(true);
				Object val = f.get(target);
				if (val == null) {
					Constructor<?> c = f.getType().getDeclaredConstructor();
					c.setAccessible(true);
					val = c.newInstance();
					f.set(target, val);
				}
				target = val;
				clazz = target.getClass();
			}

			Field f = clazz.getDeclaredField(fs[fs.length - 1]);
			f.setAccessible(true);
			f.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 枚举类型转map类型
	 *
	 * @param baseEnum
	 *            枚举类class.getEnumConstants()
	 * @return map集合，根据id,value
	 */
	public static Map enumToMap(BaseEnum[] baseEnum) {
		return BasicUtil.enumToMap(baseEnum, true);
	}

	/**
	 * 枚举类型转map类型
	 *
	 * @param baseEnum
	 *            枚举类class.getEnumConstants()
	 * @param idKey
	 *            true的id为主键，false的name为主键
	 * @return map集合
	 */
	public static Map enumToMap(BaseEnum[] baseEnum, boolean idKey) {
		Map map = new HashMap();
		for (BaseEnum be : baseEnum) {
			if (idKey) {
				map.put(be.toInt() + "", be.toString());
			} else {
				Enum e = (Enum) be;
				map.put(e.name(), be.toString());
			}
		}
		return map;
	}

	/**
	 * 资源文件转map类型
	 *
	 * @param resPath 资源文件包路径
	 * @return map集合
	 */
	public static Map resToMap(String resPath) {
		return BasicUtil.resToMap(ResourceBundle.getBundle(resPath));
	}

	/**
	 * 资源文件转map类型
	 *
	 * @param rb 资源文件
	 * @return map集合
	 */
	@SuppressWarnings("rawtypes")
	public static Map resToMap(ResourceBundle rb) {
		Map map = new HashMap();
		Enumeration e = rb.getKeys();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			map.put(key, rb.getString(key));
		}
		return map;
	}

	/**
	 * 获取Cookie的值
	 *
	 * @param key
	 *            枚举类中的值
	 * @return Cookie中获取的对象
	 */
	public static String getCookie(BaseCookieEnum key) {
		HttpServletRequest request = SpringUtil.getRequest();
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals(key.toString())) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 获取Cookie的值
	 *
	 * @param key
	 *            字符串
	 * @return Cookie中获取的对象
	 */
	public static String getCookie(String key) {
		HttpServletRequest request = SpringUtil.getRequest();
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals(key)) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 设置Cookie值，浏览器关闭cookie会被删除
	 *
	 * @param response
	 *            HttpServletResponse对象
	 * @param key
	 *            枚举类中的值
	 * @param value
	 *            存储对象
	 */
	public static void setCookie(HttpServletResponse response, BaseCookieEnum key, Object value) {
		if(value == null) {
			BasicUtil.setCookie(response, null, "/", key.toString(), null, -1);
		} else {
			BasicUtil.setCookie(response, null, "/", key.toString(), value.toString(), -1);
		}
	}

	/**
	 * 获取当前登陆管理员信息(替代session方式获取)
	 * @return 没有登陆返回 null
	 */
	public static ManagerEntity getManager() {
		Subject subject = SecurityUtils.getSubject();
		if(subject.getPrincipal() instanceof ManagerEntity) {
			return  (ManagerEntity) subject.getPrincipal();
		}
		return null;
	}

	/**
	 * 设置Cookie值
	 *
	 * @param response
	 *            HttpServletResponse对象
	 * @param key
	 *            枚举类中的值
	 * @param value
	 *            存储对象
	 * @param expiry
	 *            过期时间，单位秒
	 */
	public static void setCookie(HttpServletResponse response, BaseCookieEnum key, Object value, int expiry) {
		BasicUtil.setCookie(response, null, "/", key.toString(), value.toString(), expiry);
	}

	/**
	 * 设置Cookie值
	 * @param response
	 *            HttpServletResponse对象
	 * @param key
	 *            枚举类中的值
	 * @param value
	 *            存储对象
	 * @param expiry
	 *            过期时间，单位秒
	 */
	public static void setCookie(HttpServletResponse response,String key, String value, int expiry) {
		BasicUtil.setCookie(response, null, "/", key.toString(), value.toString(), expiry);
	}

	/**
	 * 设置Cookie值
	 * @param response
	 *            HttpServletResponse对象
	 * @param domain 域
	 * @param key
	 *            枚举类中的值
	 * @param value
	 *            存储对象
	 * @param expiry
	 *            过期时间，单位秒
	 */
	public static void setCookie(HttpServletResponse response, String domain,String key, String value, int expiry) {
		BasicUtil.setCookie(response, domain, "/", key.toString(), value.toString(), expiry);
	}

	/**
	 * 设置cooke
	 * @param response
	 * @param domain 域 如：baidu.com 没有www，没有端口
	 * @param path 路径
	 * @param key 名称
	 * @param value 值
	 * @param expiry 过期时间,单位秒 默认0，关闭窗口后该Cookie即失效 0删除cookie
	 */
	public static void setCookie(HttpServletResponse response,String domain, String path, String key, String value, int expiry) {
		if(StringUtils.isEmpty(key)) {
			return;
		}
		Cookie cookie = new Cookie(key.toString(), (String) value);
		if(StringUtils.isNotEmpty(path)) {
			cookie.setPath(path);
		}
		if(StringUtils.isNotEmpty(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setMaxAge(expiry);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}


	/**
	 * 根据当前请求的特征，判断该请求是否来自手机终端，主要检测特殊的头信息，以及user-Agent这个header
	 *
	 * @return 如果命中手机特征规则，则返回对应的特征字符串
	 */
	public static boolean isMobileDevice() {
		HttpServletRequest request = SpringUtil.getRequest();
		boolean b = false;
		boolean pcFlag = false;
		boolean mobileFlag = false;
		String via = request.getHeader("Via");
		String userAgent = request.getHeader("user-agent");
		for (int i = 0; via != null && !via.trim().equals("") && i < mobileGateWayHeaders.length; i++) {
			if (via.contains(mobileGateWayHeaders[i])) {
				mobileFlag = true;
				break;
			}
		}
		for (int i = 0; !mobileFlag && userAgent != null && !userAgent.trim().equals("")
				&& i < mobileUserAgents.length; i++) {
			if (userAgent.contains(mobileUserAgents[i])) {
				mobileFlag = true;
				break;
			}
		}
		for (int i = 0; userAgent != null && !userAgent.trim().equals("") && i < pcHeaders.length; i++) {
			if (userAgent.contains(pcHeaders[i])) {
				pcFlag = true;
				break;
			}
		}
		if (mobileFlag == true && pcFlag == false) {
			b = true;
		}
		return b;// false pc true shouji

	}


	/**
	 * 判断是否是微信内置浏览器<br/>
	 * 如果用来判断是否为手机端的微信内置浏览器需和isMobileDevice()方法一起使用
	 * @return true：为微信内置浏览器
	 *         false: 不是微信内置浏览器
	 */
	public static boolean isWechatBrowser(){
		HttpServletRequest request = SpringUtil.getRequest();
		boolean isWechatBrowser = false;
		String ua = request.getHeader("user-agent")
				.toLowerCase();
		if (ua.indexOf("micromessenger") > 0){
			isWechatBrowser = true;
		}
		return isWechatBrowser;
	}

	/**
	 * 判断是否为ajax XMLHttpRequest
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		if ((request.getHeader("accept")!=null && request.getHeader("accept").equals("*/*"))
				|| ((request.getHeader("X-Requested-With") != null
				&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
			return true;
		}else {
			return false;

		}
	}

	/**
	 * 过滤字段
	 * 此方法不推荐使用，推荐使用@@JsonIgnore 实体字段注解方式，
	 * 注意：hutool的工具会将时间变成时间戳，所以次方法默认将日期格式全部转换为 yyyy-MM-dd hh:mm:ss
	 * @param obj 过滤对象
	 * @param filters  过滤参数
	 * @return 过滤后的对象
	 */

	public static <T> T filter(T obj,final String... filters) {
		if (obj == null) {
			return null;
		}
		List list = Arrays.asList(filters);
		// TODO: 2023/1/13 由于JSONUtil 暂时不支持 @JsonForma 的格式，只能默认设定一个格式
		String str = JSONUtil.parseObj(obj).setDateFormat("yyyy-MM-dd HH:mm:ss").toJSONString(0, new Filter<MutablePair<Object, Object>>() {
			@Override
			public boolean accept(MutablePair<Object, Object> objectObjectMutablePair) {
				return !list.contains(objectObjectMutablePair.getKey());
			}
		});
		return (T) JSONUtil.toBean(str,obj.getClass());
	}

	/**
	 * 输出json数据,已弃用
	 * 请使用springmvc的@ResponseBody返回{@link ResultData}包装对象
	 *
	 * @param response
	 *            HttpServletResponse对象
	 * @param dataStr
	 *            字符串
	 */
	public static void outString(HttpServletResponse response, String dataStr) {
		try {
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(dataStr);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 获取文件真实类型
	 * @param fileInput 文件流
	 * @param fileName  文件名
	 * @return
	 */
	public static String getMimeType(InputStream fileInput, String fileName) {

		AutoDetectParser parser = new AutoDetectParser();
		parser.setParsers(new HashMap<>());
		Metadata metadata = new Metadata();
		String fileType = FileNameUtil.extName(fileName);
		try  {
			parser.parse(fileInput, new DefaultHandler(), metadata, new ParseContext());
		} catch (ZeroByteFileException e) {
			return fileType;
		} catch (Exception e) {
			e.getStackTrace();
		}
		String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);
		if (contentType.contains("/")){
			fileType = contentType.substring(contentType.lastIndexOf("/") + 1);
		}
		if (fileType.contains("-")){
			return fileType.substring(fileType.lastIndexOf("-") + 1);
		}
		return fileType;
	}

	/**
	 * 获取当前请求地址，包括参数会才有?a=1&b=1方式组装成一条新的完整地址
	 * @param request 对象
	 * @return 一条完整的get请求地址
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		Map<String, String[]> map = request.getParameterMap();
		return requestURL.append(CollectionUtil.isNotEmpty(map)?("?"+ HttpUtil.toParams(map)) : "")+"";
	}


}
