package com.zzj.data.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.*;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author TangXianRong
 * @since 2016年8月4日
 * @version 数据库工具类
 */
public class DbTool {
	/** 日志 */
	protected static Log log = LogFactory.getLog(DbTool.class);
	//TODO 无配置文件 报错，需要处理
//	/* oracle标识 */
//	public static final String DB_ORACLE_TAG = Config.getProperty("sys.db.oracle.tag", "oracle");
//	/* mssql标识 */
//	public static final String DB_MSSQL_TAG = Config.getProperty("sys.db.mssql.tag", "mssql");
//	/* db2标识 */
//	public static final String DB_DB2_TAG = Config.getProperty("sys.db.db2.tag", "db2");
//	/* mysql标识 */
//	public static final String DB_MYSQL_TAG = Config.getProperty("sys.db.mysql.tag", "mysql");
//	/* sqlite标识 */
//	public static final String DB_SQLITE_TAG = Config.getProperty("sys.db.sqlite.tag", "sqlite");
//	/* hsqldb标识 */
//	public static final String DB_HSQLDB_TAG = Config.getProperty("sys.db.hsqldb.tag", "hsqldb");
//	/* postgresql标识 */
//	public static final String DB_POSTGRESQL_TAG = Config.getProperty("sys.db.postgresql.tag", "postgresql");
//	/* dbflat标识 */
//	public static final String DB_BDF_TAG = Config.getProperty("sys.db.bdf.tag", "bdf");
//	/* dm标识 */
//	public static final String DB_DM_TAG = Config.getProperty("sys.db.dm.tag", "dm");
//	/* mpp标识 */
//	public static final String DB_MPP_TAG = Config.getProperty("sys.db.mppdb.tag", "mppdb");
//	/* gp标识 */
//	public static final String DB_GP_TAG = Config.getProperty("sys.db.greenplum.tag", "greenplum");
//	/* sybase标识 */
//	public static final String DB_SYBASE_TAG = Config.getProperty("sys.db.sybase.tag", "sybase");

	/* oracle标识 */
	public static final String DB_ORACLE_TAG = "oracle";
	/* mssql标识 */
	public static final String DB_MSSQL_TAG = "mssql";
	/* db2标识 */
	public static final String DB_DB2_TAG = "db2";
	/* mysql标识 */
	public static final String DB_MYSQL_TAG = "mysql";
	/* sqlite标识 */
	public static final String DB_SQLITE_TAG = "sqlite";
	/* hsqldb标识 */
	public static final String DB_HSQLDB_TAG = "hsqldb";
	/* postgresql标识 */
	public static final String DB_POSTGRESQL_TAG = "postgresql";
	/* dbflat标识 */
	public static final String DB_BDF_TAG = "bdf";
	/* dm标识 */
	public static final String DB_DM_TAG = "dm";
	/* mpp标识 */
	public static final String DB_MPP_TAG = "mppdb";
	/* gp标识 */
	public static final String DB_GP_TAG = "greenplum";
	/* sybase标识 */
	public static final String DB_SYBASE_TAG = "sybase";

	/**
	 * 获取得到某个表字段信息的SQL语句
	 *
	 * @param dbType
	 *            数据库类型 示例：oracle mssql db2 mysql sqlite hsqldb postgresql
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQL(String dbType, String tabCode) throws Exception {
		String result = "";
		dbType = dbType.toLowerCase();
		if (DB_ORACLE_TAG.equals(dbType)) {
			return getFieldInfoSQLByOracle(tabCode);
		} else if (DB_MSSQL_TAG.equals(dbType)) {
			return getFieldInfoSQLByMssql(tabCode);
		} else if (DB_DB2_TAG.equals(dbType)) {
			return getFieldInfoSQLByDb2(tabCode);
		} else if (DB_MYSQL_TAG.equals(dbType)) {
			return getFieldInfoSQLByMysql(tabCode);
		} else if (DB_SQLITE_TAG.equals(dbType)) {
			return getFieldInfoSQLBySqlite(tabCode);
		} else if (DB_HSQLDB_TAG.equals(dbType)) {
			return getFieldInfoSQLByHsqldb(tabCode);
		} else if (DB_POSTGRESQL_TAG.equals(dbType)) {
			return getFieldInfoSQLByPostgresql(tabCode);
		} else if (DB_DM_TAG.equals(dbType)) {
			return getFieldInfoSQLByDM(tabCode);
		} else if (DB_MPP_TAG.equals(dbType)) {
			return getFieldInfoSQLByPostgresql(tabCode); // TODO 暂空缺
		} else if (DB_SYBASE_TAG.equals(dbType)) {
			return getFieldInfoSQLBySybase(tabCode);
		}
		return result;
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 oracle
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByOracle(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.TABLE_NAME as TAB_NAME, ");
		sql.append("        A.COLUMN_ID as COL_ORDER, ");
		sql.append("        A.COLUMN_NAME as CODE, ");
		sql.append("        A.DATA_TYPE as TYPE_NAME, ");
		sql.append("        A.DATA_LENGTH as LEN, ");
		sql.append("        case ");
		sql.append("          when A.NULLABLE = 'N' then ");
		sql.append("           '0' ");
		sql.append("          else ");
		sql.append("           '1' ");
		sql.append("        end as IS_NULL, ");
		sql.append("        A.DATA_SCALE as DEC_PLACES, ");
		sql.append("        A.DATA_DEFAULT as VAL_DEF, ");
		sql.append("        CASE ");
		sql.append("          WHEN A.Column_Name in ");
		sql.append("               (select col.column_name ");
		sql.append("                  from user_constraints con, user_cons_columns col ");
		sql.append("                 where con.constraint_name = col.constraint_name ");
		sql.append("                   and con.constraint_type = 'P' ");
		sql.append("                   and col.table_name = A.Table_Name) THEN ");
		sql.append("           '1' ");
		sql.append("          ELSE ");
		sql.append("           '0' ");
		sql.append("        END as IS_PRIKEY, ");
		sql.append("        B.comments as EXPLAIN ");
		sql.append("   FROM USER_TAB_COLS A ");
		sql.append("  inner join user_col_comments B on B.TABLE_NAME = A.TABLE_NAME ");
		sql.append("                                and B.COLUMN_NAME = A.COLUMN_NAME ");
		sql.append("   left join user_tab_columns C on A.TABLE_NAME = C.TABLE_NAME ");
		sql.append("                               and A.Column_Name = C.COLUMN_NAME ");
		sql.append("  where A.TABLE_NAME = '" + tabCode + "' ");

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 oracle
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLBySybase(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(
				" select a.name as COLUMN_NAME,(select b.name from dbo.systypes b where b.usertype=a.usertype and a.type=b.type) AS COLUMN_TYPE,");
		sql.append(
				" (case when scale is null then CONVERT(varchar,a.length) else CONVERT(varchar,a.length) + ',' + CONVERT(varchar,a.scale) END) as DATALENGTH,null as COMMENT,");
		sql.append(" (case when c.COLUMNNAME is not null then 'P' else null end) as dbKeyFlg ");
		sql.append(" from dbo.syscolumns a  left join ");
		sql.append(
				" (SELECT COL_NAME(I.id,I.indid) COLUMNNAME,I.id TABLEID  FROM sysindexes I,sysobjects O,syssegments S  ");
		sql.append(
				" WHERE I.id=O.id AND I.status2 & 2 = 2 AND I.status & 2048 = 2048 AND I.indid>0 AND I.segment=S.segment) c ");
		sql.append(" on a.id=c.TABLEID and a.name=c.COLUMNNAME ");
		sql.append("  where a.id=OBJECT_ID('" + tabCode + "') ");

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 --mssql
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByMssql(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("  select  d.name as TAB_NAME,a.colorder as COL_ORDER, ");
		sql.append("  a.name as CODE, b.name as TYPE_NAME , ");
		sql.append("  COLUMNPROPERTY(a.id, a.name, 'PRECISION')  as LEN, ");
		sql.append("  a.length as SIZE,");
		sql.append("  isnull(COLUMNPROPERTY(a.id, a.name, 'Scale'), 0) as DEC_PLACES , ");
		sql.append("  a.isnullable as IS_NULL ,isnull(e.text, '') as VAL_DEF, ");
		sql.append("    (CASE WHEN");
		sql.append("            (SELECT COUNT(*) FROM sysobjects WHERE ");
		sql.append("                 (name IN (SELECT name FROM sysindexes ");
		sql.append("                       WHERE (id = a.id) AND (indid IN ");
		sql.append("                       (SELECT indid FROM sysindexkeys ");
		sql.append("                             WHERE (id = a.id) AND (colid IN ");
		sql.append("                                (SELECT colid FROM syscolumns ");
		sql.append("                            WHERE (id = a.id) AND (name = a.name))))))) AND ");
		sql.append("                 (xtype = 'PK')) > 0 THEN '1' ELSE '0' END) as IS_PRIKEY , ");
		sql.append("  isnull(g.[value], '') as EXPLAIN ");
		sql.append("  FROM ");
		sql.append("      syscolumns a LEFT JOIN systypes b ON a.xtype = b.xusertype ");
		sql.append("      INNER JOIN sysobjects d ON a.id = d .id ");
		sql.append("                                AND d .xtype = 'U' AND d .name <> 'dtproperties' ");
		sql.append("      LEFT JOIN syscomments e ON a.cdefault = e.id ");
		sql.append("      LEFT JOIN sysproperties g ON a.id = g.id AND a.colid = g.smallid ");
		sql.append("  where d.name='" + tabCode + "' ");
		sql.append("      ORDER BY object_name(a.id), a.colorder ");

		return sql.toString();

	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 db2
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByDb2(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 --DM
	 */
	public static String getFieldInfoSQLByDM(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.TABLE_NAME as TAB_NAME, ");
		sql.append("        A.COLUMN_ID as COL_ORDER, ");
		sql.append("        A.COLUMN_NAME as CODE, ");
		sql.append("        A.DATA_TYPE as TYPE_NAME, ");
		sql.append("        A.DATA_LENGTH as LEN, ");
		sql.append("        case ");
		sql.append("          when A.NULLABLE = 'N' then ");
		sql.append("           '0' ");
		sql.append("          else ");
		sql.append("           '1' ");
		sql.append("        end as IS_NULL, ");
		sql.append("        A.DATA_SCALE as DEC_PLACES, ");
		sql.append("        A.DATA_DEFAULT as VAL_DEF, ");
		sql.append("        CASE ");
		sql.append("          WHEN A.Column_Name in ");
		sql.append("               (select col.column_name ");
		sql.append("                  from user_constraints con, user_cons_columns col ");
		sql.append("                 where con.constraint_name = col.constraint_name ");
		sql.append("                   and con.constraint_type = 'P' ");
		sql.append("                   and col.table_name = A.Table_Name) THEN ");
		sql.append("           '1' ");
		sql.append("          ELSE ");
		sql.append("           '0' ");
		sql.append("        END as IS_PRIKEY, ");
		sql.append("        B.comments as EXPLAIN ");
		sql.append("   FROM USER_TAB_COLS A ");
		sql.append("  inner join user_col_comments B on B.TABLE_NAME = A.TABLE_NAME ");
		sql.append("                                and B.COLUMN_NAME = A.COLUMN_NAME ");
		sql.append("   left join user_tab_columns C on A.TABLE_NAME = C.TABLE_NAME ");
		sql.append("                               and A.Column_Name = C.COLUMN_NAME ");
		sql.append("  where A.TABLE_NAME = '" + tabCode + "' ");
		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 mysql
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByMysql(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 sqlite
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLBySqlite(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 hsqldb
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByHsqldb(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}

	/**
	 * 获取得到某数据表所有字段信息的SQL语句 postgresql
	 *
	 * @param tabCode
	 *            表名代码
	 * @return
	 * @throws Exception
	 */
	public static String getFieldInfoSQLByPostgresql(String tabCode) throws Exception {
		StringBuffer sql = new StringBuffer();

		return sql.toString();
	}


	/**
	 *
	 * 功能: 将数据字段名转化为符合JAVA规范的属性名 <br>
	 * dbColumnName:从数据库中读取的字段名(需要严格按数据库命名规范来命名)
	 *
	 * @param dbColumnName
	 *            数据字段名
	 * @return 符合JAVA规范的属性名
	 * @since 1.0 by 山人
	 */
	public static String dbColumnNameToJavaName(String dbColumnName) {
		String[] columns = dbColumnName.split("_");
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < columns.length; i++) {
			if (i == 0) {
				stb.append(columns[i].toLowerCase());
			} else if (i == 1) {
				if (columns[0].length() == 1) {
					stb.append(columns[i].toLowerCase());
				} else {
					if (columns[i].length() == 1) {
						stb.append(columns[i].toLowerCase());
					} else {
						String first = columns[i].substring(0, 1);
						String second = columns[i].substring(1);
						stb.append(first.toUpperCase());
						stb.append(second.toLowerCase());
					}
				}
			} else {
				if (columns[i].length() == 1) {
					stb.append(columns[i].toLowerCase());
				} else {
					String first = columns[i].substring(0, 1);
					String second = columns[i].substring(1);
					stb.append(first.toUpperCase());
					stb.append(second.toLowerCase());
				}

			}

		}
		return stb.toString();
	}

	/**
	 * 关闭数据库连接
	 *
	 * @param conn
	 * @param pstat
	 * @param rs
	 */
	public static void closeConnection(Connection conn, Statement pstat, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("关闭数据库资源异常.",e);
			}
		}
		if (pstat != null) {
			try {
				pstat.close();
			} catch (SQLException e) {
				log.error("关闭数据库资源异常.",e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("关闭数据库资源异常.",e);
			}
		}
	}
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("关闭数据库资源异常.",e);
			}
		}
	}
	// 绑定参数
	public static void setParameter(PreparedStatement pstat,  List<?> parameters) throws SQLException {
		if (parameters == null) {
			return;
		}
		try {
			for (int i = 0; i < parameters.size(); i++) {
				if (parameters.get(i) == null) {
					pstat.setObject(i + 1, null);
				} else if (parameters.get(i) instanceof Integer) {
					pstat.setInt(i + 1, (Integer) parameters.get(i));
				} else if (parameters.get(i) instanceof String) {
					pstat.setString(i + 1, (String) parameters.get(i));
				} else if (parameters.get(i) instanceof Double) {
					pstat.setDouble(i + 1, (Double) parameters.get(i));
				}  else if (parameters.get(i) instanceof Float) {
					pstat.setFloat(i + 1, (Float) parameters.get(i));
				} else if (parameters.get(i) instanceof Long) {
					pstat.setLong(i + 1, (Long) parameters.get(i));
				}  else if (parameters.get(i) instanceof Date) {
					Date d = (Date) parameters.get(i);
					pstat.setTimestamp(i + 1, new Timestamp(d.getTime()));
				}else{
					pstat.setObject(i + 1, parameters.get(i));
				}
			}
		} catch (SQLException e) {
			log.error("DbOperatorUtil.setParameter绑定参数异常", e);
			throw e;
		}
	}

	/**
	 *
	 * 功能:oracle 数据库的 分页查询
	 * @param strSql SQL查询语句
	 * @param pageIndex 页码
	 * @param pageSize 每页显示的记录数
	 * @return PageList 分页数据集合
	 * @throws Exception
	 */
	public static String getOraclePageSQL(String strSql, int pageIndex, int pageSize) {
		StringBuffer dataSql = new StringBuffer();
		dataSql.append(" select * from ");
		dataSql.append(" (select AAAAA.*,rownum row_num from ");
		dataSql.append("  (  ");
		dataSql.append(strSql);
		dataSql.append("  ) AAAAA  ");
		dataSql.append("  ) BBBBB  ");
		dataSql.append("  where BBBBB.row_num > ").append(((pageIndex - 1) * pageSize));
		dataSql.append(" and BBBBB.row_num <=").append((pageIndex * pageSize));

		return dataSql.toString();
	}

	public static String getClobToString(Clob clob) throws Exception {
		String reString = "";
		Reader is = clob.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
			sb.append(s);
			s = br.readLine();
		}
		reString = sb.toString();
		return reString;
	}

	public static String castBlobToString(Blob blob)throws Exception{
		String blobString = new String(blob.getBytes(1, (int) blob.length()),"UTF-8");
		return blobString;
	}

}
