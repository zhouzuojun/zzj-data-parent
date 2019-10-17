package com.zzj.data.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzj.data.params.DbParams;
import com.zzj.data.util.DbTool;
import com.zzj.data.util.RowKeyUtil;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DbBuilder {

	private static Logger logger = LoggerFactory.getLogger(DbBuilder.class);

	/**
	 * 关系型数据库默认配置
	 */
	private static Properties rdmsDatasourceConfig;

	/**
	 * 类加载器缓存池
	 */
	public static Map<String, ClassLoader> ClassLoaderMap = new ConcurrentHashMap<String, ClassLoader>();

	/**
	 * 各数据库的默认配置
	 */
	public static Map<String, DbParams> RDBMSDsParamsMap = new ConcurrentHashMap<String, DbParams>();

	/**
	 * 数据源缓存池
	 */
	private static Map<String, DbDatasource> RDBMSDatasoutceMap = new ConcurrentHashMap<String, DbDatasource>();

	static {
		// 初始化 MappingDriverUrlMap
		// 初始化 QueryDbTimeSql
		if (RDBMSDsParamsMap == null || RDBMSDsParamsMap.size() == 0) {
			init();
		}
	}

	/**
	 * 从配置文件rdmsDatasource-mapping.xml读取信息初始化
	 */

	private static void init() {
		InputStream in = null;
		try {
			in = DbBuilder.class.getResourceAsStream("/rdmsDatasourceConfig.properties");
			rdmsDatasourceConfig = new Properties();
			rdmsDatasourceConfig.load(in);
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		} catch (IOException e) {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					logger.error("",ex);
				}
			}
			logger.error("",e);
		}
		try {
			XMLConfiguration config = new XMLConfiguration("rdmsDatasource-mapping.xml");
			// 禁用分隔符，要在读取XML文件前设置
			config.setDelimiterParsingDisabled(true);
			List<Object> ls = config.getList("start-mapping.mapping.type");
			String type = null;
			String datasourceImplClass = null;
			String queryDbTimeSql = null;
			String driverClass = null;
			DbParams params = null;

			for (int i = 0; i < ls.size(); i++) {
				type = config.getString("start-mapping.mapping(" + i + ").type");
				datasourceImplClass = config.getString("start-mapping.mapping(" + i + ").datasourceImplClass");
				queryDbTimeSql = config.getString("start-mapping.mapping(" + i + ").queryDbTimeSql");
				driverClass = config.getString("start-mapping.mapping(" + i + ").driverClass");

				params = new DbParams();
				params.setType(type);
				params.setDatasourceImplClass(datasourceImplClass);
				params.setQueryDbTimeSql(queryDbTimeSql);
				params.setDriverClassName(driverClass);

				RDBMSDsParamsMap.put(type, params);
			}
		} catch (Exception e) {
			logger.error("初始化数据源配置时异常", e);
		}
	}
	/**
	 * 获取对应数据库的默认配置
	 *
	 * @param dbType
	 *            数据库类型
	 * @return
	 */
	private static DbParams getDsParams(String dbType) {
		DbParams dsParam = RDBMSDsParamsMap.get(dbType);
		if (dsParam == null) {
			throw new RuntimeException("目前暂不支持该关系型数据源。");
		}
		return dsParam;
	}

	/**
	 * 获取数据源
	 *
	 * @method: get
	 * @param dataSourceId
	 *            数据源ID
	 * @return 数据源
	 * @throws Exception
	 */
	public static DbDatasource get(Long dataSourceId) {
		if (null == dataSourceId) {
			return null;
		} else {
			return RDBMSDatasoutceMap.get(dataSourceId.toString());
		}
	}

	/**
	 * 构建数据源
	 *
	 * @param params
	 *            数据源参数
	 * @return
	 */
	public static DbDatasource build(DbParams params) throws Exception {
		long time = System.currentTimeMillis();
		//没有数据源id的情况下 组装数据源id
		if (StringUtils.isEmpty(params.getDsId())) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(params.getUser());
			buffer.append(params.getPassword());
			buffer.append(params.getUrl());
			params.setDsId(RowKeyUtil.createRowKey(buffer.toString()));
		}
		DbDatasource ds = RDBMSDatasoutceMap.get(params.getDsId());
		if (ds != null) {
			return ds;
		}
		DbParams dsParam = getDsParams(params.getType());
		if (!StringUtils.isEmpty(dsParam.getDatasourceImplClass())) {
			ds = (DbDatasource) Class.forName(dsParam.getDatasourceImplClass())
					.getDeclaredConstructor(DruidDataSource.class, DbParams.class)
					.newInstance(createBasicDataSource(params), params);
		} else {
			throw new RuntimeException("目前暂不支持该关系型数据源。");
		}
		RDBMSDatasoutceMap.put(params.getDsId(), ds);// 添加数据源
		logger.info("创建dataSource耗时时长："+(System.currentTimeMillis()-time));
		return ds;
	}
	public static DruidDataSource createBasicDataSource(DbParams params) throws Exception {
		DruidDataSource tds = new DruidDataSource();
		DbParams dsParam = getDsParams(params.getType());
		tds.setDriverClassName(dsParam.getDriverClassName());
		tds.setPassword(params.getPassword());
		tds.setUrl(params.getUrl());
		tds.setUsername(params.getUser());
//		tds.setBreakAfterAcquireFailure(true);
		if (params.isCheckIpPort()) {
			try {
				// 测试URL的ip端口,如果不通则不进行数据库连接初始化
				String[] ipPort = getIpPortByJdbcUrl(params.getUrl());
				if (ipPort == null) {
					throw new RuntimeException("数据库连接串配置错误 !");
				}
				if (false == pingHostPort(ipPort[0], Integer.valueOf(ipPort[1]))) {
					throw new RuntimeException("ping测试不通过。");
				}
			} catch (Exception ex) {
				logger.error("ping测试不通过。error=", ex);
				throw ex;
			}
		}
		ClassLoader classLoader = getClassLoaderByDbType(params.getType());
		if (classLoader != null) {
			tds.setDriverClassLoader(classLoader);
		}
		// 初始化大小
		tds.setInitialSize(Integer.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolInitialSize", "3")));
		tds.setMinIdle(Integer.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolMinIdle", "3")));
		tds.setMaxActive(Integer.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolMaxActive", "200")));
		// 获取连接等待时间
		tds.setMaxWait(Integer.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolMaxWait", "60000")));
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		tds.setTimeBetweenEvictionRunsMillis(Integer
				.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolTimeBetweenEvictionRunsMillis", "60000")));
		// 配置一个连接在池中最小生存时间，单位是毫秒
		tds.setMinEvictableIdleTimeMillis(Integer
				.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolMinEvictableIdleTimeMillis", "300000")));
		// 校验sqlSELECT sysdate FROM DUAL
		// oracle ,mysql等关系型数据库测试sql: select 1 from dual
		// greenplum 比较特殊 使用 select now() as sysdate
		if (params.getType().contains("103")) {// 103 代表greenplum数据库
			tds.setValidationQuery("select now() as sysdate");
		} else {
			tds.setValidationQuery(rdmsDatasourceConfig.getProperty("DataSourceTestSQL", "select 1 from dual"));
		}
		tds.setTestWhileIdle(Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourceTestWhileIdle", "true")));
		tds.setTestOnBorrow(Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourceTestOnBorrow", "false")));
		tds.setTestOnReturn(Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourceTestOnReturn", "false")));
		tds.setPoolPreparedStatements(
				Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourcePoolPreparedStatements", "true")));
		tds.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(
				rdmsDatasourceConfig.getProperty("DataSourceMaxPoolPreparedStatementPerConnectionSize", "20")));
		tds.setUseOracleImplicitCache(false);
		tds.setConnectionErrorRetryAttempts(5);// default value is 30
		tds.setBreakAfterAcquireFailure(true);// default value is false
		tds.setRemoveAbandoned(Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourceConPoolRemoveAbandoned", "true")));
		tds.setRemoveAbandonedTimeout(Integer.parseInt(rdmsDatasourceConfig.getProperty("DataSourceConPoolRemoveAbandonedTimeout", "1800")));
		tds.setLogAbandoned(Boolean.parseBoolean(rdmsDatasourceConfig.getProperty("DataSourceConPoolLogAbandoned", "true")));
		return tds;
	}

	/**
	 * 销毁数据源
	 */
	public static synchronized void destory(String dsId) {
		DbDatasource ds = RDBMSDatasoutceMap.remove(dsId);
		if (ds != null) {
			ds.destory();
			ds = null;
		}
	}

	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * 获取类加载器
	 *
	 * @param dbType
	 *            db type
	 * @return
	 */
	public static ClassLoader getClassLoaderByDbType(String dbType) throws Exception {
		if (StringUtils.isEmpty(dbType)) {
			return null;
		}
		ClassLoader cl = ClassLoaderMap.get(dbType);
		if (cl != null) {
			return cl;
		}

		lock.lock();
		try {

			cl = ClassLoaderMap.get(dbType);
			if (cl == null) {
				DbParams dsParam = getDsParams(dbType);
				final URL[] urls = dsParam.getDriverUrls();
				if (urls != null) {
					URLClassLoader classLoader = new URLClassLoader(urls);
					ClassLoaderMap.put(dbType, classLoader);
				}
			}
		} finally {
			lock.unlock();
		}
		return ClassLoaderMap.get(dbType);
	}

	/**
	 * 构建ping串
	 *
	 * @param url
	 * @return
	 */
	public static String[] getIpPortByJdbcUrl(String url) {
		String[] ipPort = new String[] { "", "" };
		String ip = "";
		String port = "";
		try {
			String[] urlArr = url.split("\\.");
			if (urlArr.length != 4) {
				return null;
			}
			String oneChar = "";
			String endChar = "";
			for (int i = urlArr[0].length() - 1; i >= 0; i--) {
				if (isNumber(String.valueOf(urlArr[0].charAt(i)))) {
					oneChar = String.valueOf(urlArr[0].charAt(i)) + oneChar;
				} else {
					break;
				}
			}
			for (int j = 0; j < 3; j++) {
				if (isNumber(String.valueOf(urlArr[3].charAt(j)))) {
					endChar = endChar + String.valueOf(urlArr[3].charAt(j));
				} else {
					break;
				}
			}
			String[] portArr = (urlArr[3]).split(":");
			for (int k = 0; k < portArr[1].length(); k++) {
				if (isNumber(String.valueOf(portArr[1].charAt(k)))) {
					port = port + String.valueOf(portArr[1].charAt(k));
				} else {
					break;
				}
			}
			ip = oneChar + "." + urlArr[1] + "." + urlArr[2] + "." + endChar;
		} catch (Exception e) {
			logger.error("解析数据库URL中的IP地址失败", e);
		}
		ipPort[0] = ip;
		ipPort[1] = port;
		return ipPort;
	}

	/**
	 * 判断是否纯数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ping测试
	 *
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean pingHostPort(String ip, int port) {
		// 先测ip是否通，不通则直接返回false
		if (false == pingHost(ip)) {
			return false;
		}
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			socket.setSoTimeout(3000);
			if (socket.isConnected()) {
				return true;
			}
		} catch (UnknownHostException e) {
			logger.error("eroor=", e);
		} catch (IOException e) {
			logger.error("eroor=", e);
		} finally {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("error=", e);
				}
			}
		}
		return false;
	}

	/**
	 * 功能：ping主机
	 *
	 * @param ip
	 * @return
	 */
	public static boolean pingHost(String ip) {
		try {
			InetAddress.getByName(ip);
		} catch (UnknownHostException e1) {
			logger.error("eroor=", e1);
			return false;
		}
		return true;
	}

	/**
	 * 测试连接情况
	 *
	 * @param dsParams
	 *            连接配置，必须包含
	 *            password（密文）、username、url、isCheckIpPort、dsType、dbType
	 * @return 正常返回1，其他则为异常
	 */
	public static boolean testConnection(DbParams dsParams) {
		DruidDataSource tds = null;
		Connection conn = null;
		Statement state = null;
		boolean normal = false;
		try {
			dsParams.setMaxActive(1);
			tds = build(dsParams);
			conn = tds.getConnection();
			state = conn.createStatement();
//			ResultSet rs = state.executeQuery("SELECT 1 FROM DUAL");
//			if (rs.next())logger.info(rs.getString(1));
			normal = true;
		} catch (Exception e) {
			normal = false;
			logger.error("testConnection：判断一个连接是否正常访问时，发生异常", e);

		} finally {
			DbTool.closeConnection(conn, state, null);
		}
		return normal;
	}

	/**
	 *
	 * @method: removeDataSource
	 * @Description: 数据源失效时, 连接池移除数据源
	 * @param dataSourceId
	 *            void 返回类型
	 */
	public static void removeDataSource(String dataSourceId) {
		DbDatasource ds = RDBMSDatasoutceMap.remove(dataSourceId);
		if (ds != null) {
			ds.destory();
			ds = null;
		}
	}
}
