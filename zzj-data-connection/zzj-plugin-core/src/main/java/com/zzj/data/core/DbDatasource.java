package com.zzj.data.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzj.data.params.*;
import com.zzj.data.util.DbTool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class DbDatasource extends DruidDataSource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 销毁
     */
    public void destory() {
        if (dbcp != null) {
            try {
                dbcp.close();
            } catch (Exception e) {
                logger.error("销毁数据源操作实体对象时，关闭销毁dbcp数据源连接池异常", e);
            }
        }
        params = null;
        dbcp = null;
    }

    protected DruidDataSource dbcp = null;

    protected DbParams params = null;

    /**
     * 获取预览数据的SQL模板
     *
     * @return
     */
    public abstract String getPreQueryDataSQL();

    /**
     * 查询表信息列表的SQL模板
     *
     * @return
     */
    public abstract String getQueryTablesSQL();

    /**
     * 查询字段信息列表的SQL模板
     *
     * @return
     */
    public abstract String getQueryFieldsSQL();

    /**
     * 获取字段查询SQL模板
     *
     * @return
     */
    public abstract String getDataByFieldsSizeSQL();

    public abstract String getQueryfieldsPartitionsql();

    /**
     * 获取表查询分页SQL
     * @param sbf
     * @param pageNum
     * @param pageSize
     * @return
     */
    public abstract  String getQueryTableByPageSQL(StringBuffer sbf ,int pageNum,int pageSize);

    /**
     * 预览数据
     *
     * @param table
     * @param size
     * @return
     */
    public List<LinkedHashMap<String, Object>> preQueryData(TableParams table, int size) throws Exception {
        return this.queryList(this.getPreQueryDataSQL().replaceAll("#owner#", table.getSchemaname()).replaceAll("#tableName#", table.getTableName()).replaceAll("#size#", String.valueOf(size)));

    }

    /**
     * 根据字段预览数据
     *
     * @param table
     * @param columnParams
     * @return
     */
    public List<Object> preQueryColumnData(TableParams table, ColumnParams columnParams) {

        return null;
    }

    public List<LinkedHashMap<String, Object>> queryList(String sql) throws Exception {
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        List<LinkedHashMap<String, Object>> rsList = new ArrayList<>();
        try {
            conn = dbcp.getConnection();
            conn = dbcp.getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while (rs.next()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(count);
                for (int i = 1; i <= count; i++) {
                    int type = metaData.getColumnType(i);
                    Object object = null;
                    String columnName = metaData.getColumnName(i);
                    try {
                        object = rs.getObject(i);
                    } catch (Exception e) {
                        map.put(columnName, "OBJECT");
                        continue;
                    }
                    if (null == object) {
                        map.put(columnName, null);
                        continue;
                    }
                    switch (type) {
                        case Types.BLOB:
                            map.put(columnName, "<BLOB>");
                            break;
                        case Types.CLOB:
                            map.put(columnName, "<CLOB>");
                            break;
                        case Types.DATE:
                            map.put(columnName, DF.format(object));
                            break;
                        case Types.TIMESTAMP:
                            map.put(columnName, DF.format(new Date(rs.getTimestamp(i).getTime())));
                            break;
                        case Types.TIME:
                            map.put(columnName, DF.format(new Date(rs.getTime(i).getTime())));
                            break;
                        case Types.LONGNVARCHAR:
                            map.put(columnName, "<LONGNVARCHAR>");
                            break;
                        case Types.LONGVARBINARY:
                            map.put(columnName, "<LONGVARBINARY>");
                            break;
                        default:
                            map.put(columnName, object);
                            break;
                    }
                }
                rsList.add(map);
            }
        } catch (Exception e) {
            logger.error("查询数据异常，请检查sql={}是否正确", sql, e);
            throw e;
        } finally {
            DbTool.closeConnection(conn, statement, rs);
        }
        return rsList;
    }


    /**
     * 查询表
     * @param userDbName
     * @param tableName
     * @param type
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<TableObj> queryTableList(String userDbName, String tableName, Integer type, Integer pageIndex, Integer pageSize)
            throws Exception {
        logger.info("开始调用 queryTableList 方法");
        long startTime = System.currentTimeMillis();
        String modeSql = buildQueryTablesSQL(getQueryTablesSQL(), userDbName, tableName, type, pageIndex, pageSize,
                false);

        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        List<TableObj> rsList = new ArrayList<TableObj>();
        try {
            long time = System.currentTimeMillis();
            conn = dbcp.getConnection();
            logger.info("关系型获取连接耗时："+(System.currentTimeMillis()-time));
            state = conn.createStatement();
            rs = state.executeQuery(modeSql);
            TableObj map = null;
            while (rs.next()) {
                map = new TableObj();
                map.setOwnerAndObjectName(rs.getString(1));
                map.setUserDbName(rs.getString(2));
                map.setTableName(rs.getString(3));
                map.setType(rs.getString(4));
                map.setComment(rs.getString(5));
                rsList.add(map);
            }
        } catch (Exception e) {
            logger.error("查询表信息列表异常", e);
            throw e;
        } finally {
            DbTool.closeConnection(conn, state, rs);
        }
        logger.info("start-tableList-end耗时："+(System.currentTimeMillis()-startTime));
        return rsList;
    }

    public int queryTableListCount(String userDbName, String tableName, Integer type, Integer pageIndex, Integer pageSize)throws Exception{
        logger.info("开始调用 queryTableListCount 方法");
        long startTime = System.currentTimeMillis();
        String modeSql = buildQueryTablesSQL(getQueryTablesSQL(), userDbName, tableName, type, pageIndex, pageSize,true);
        int count=0;
        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        try {
            long time = System.currentTimeMillis();
            conn = dbcp.getConnection();
            logger.info("关系型获取连接耗时："+(System.currentTimeMillis()-time));
            state = conn.createStatement();
            rs = state.executeQuery(modeSql);
            while (rs.next()) {
                count=rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("查询表信息列表异常", e);
            throw e;
        } finally {
            DbTool.closeConnection(conn, state, rs);
        }
        logger.info("start-tableList-end耗时："+(System.currentTimeMillis()-startTime));
        return count;
    }



    /**
     * 查询表信息列表
     *
     * @param modeSql
     *            各数据库查询表信息列表的SQL
     * @param userDbName
     *            用户或实例名
     * @param tableName
     *            表名，模糊查询
     * @param type
     *            表类型，0=不区分类型查，1=表，2=视图，3=Type（ES索引里的type）
     * @param isCount
     *            是否为构建统计SQL
     * @return
     * @throws Exception
     */
    private String buildQueryTablesSQL(String modeSql, String userDbName, String tableName, Integer type, Integer pageIndex,
                                       Integer pageSize, boolean isCount) {
        StringBuffer sqlBuffer = null;
        if (isCount) {
            sqlBuffer = new StringBuffer("SELECT count(1) FROM (");
        } else {
            sqlBuffer = new StringBuffer("SELECT TEMP.* FROM (");
        }
        sqlBuffer.append(modeSql);
        sqlBuffer.append(") TEMP WHERE 1=1 ");

        if(type!=null){
            if (type == 1) {
                sqlBuffer.append(" AND TEMP.OBJECTTYPE = 'TABLE'");
            } else if (type == 2) {
                sqlBuffer.append(" AND TEMP.OBJECTTYPE = 'VIEW'");
            }
        }

        if (!StringUtils.isEmpty(userDbName)) {
            sqlBuffer.append(" AND upper(TEMP.OWNER)='").append(userDbName.trim().toUpperCase()).append("'");
        }
        if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(tableName.trim())) {
            sqlBuffer.append(" AND upper(TEMP.OBJECTNAME) like '%").append(tableName.trim().toUpperCase()).append("%'");
        }
        if (!isCount && pageIndex > 0 && pageSize > 0) {
            return getQueryTableByPageSQL(sqlBuffer,pageIndex,pageSize);
        } else {
            return sqlBuffer.toString();
        }
    }


    /**
     * 查询字段列表
     * @param table
     * @return
     * @throws Exception
     */
    public List<FieldObj> queryFieldList(TableObj table) throws Exception {
        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        try {
            conn = dbcp.getConnection();
            state = conn.createStatement();
            String sql = getQueryFieldsSQL();
            sql = sql.replace("#owner#", table.getUserDbName()).replace("#tableName#", table.getTableName());
            rs = state.executeQuery(sql);
            List<FieldObj> rsList = new ArrayList<FieldObj>();
            FieldObj fieldObj = null;
            List<Map<?, ?>> pfList=getPartionField(table,rs);
            List<String> cn=new ArrayList<>();
            List<String> pn=new ArrayList<>();

            while (rs.next()) {
                fieldObj = new FieldObj();
                fieldObj.setColumnName(rs.getString(1));
                fieldObj.setType(rs.getString(2));
                fieldObj.setLength(rs.getInt(3));
                fieldObj.setComment(rs.getString(4));
                if("Y".equalsIgnoreCase(rs.getString(5))){
                    fieldObj.setDbKeyFlg("1");
                }else{
                    fieldObj.setDbKeyFlg("0");
                }
                if(pfList!=null&&!pfList.isEmpty()){
                    for(Map pfMap :pfList){
                        cn.add(pfMap.get("column_name").toString());
                        pn.add(pfMap.get("partition_name").toString());
                    }
                    if(rs.getString(1).equalsIgnoreCase(cn.get(0)))
                        fieldObj.setPartitions(pn);
                }
                fieldObj.setColIndex(rs.getString(6));
                rsList.add(fieldObj);
            }
            return rsList;
        } catch (Exception e) {
            logger.error("查询表结构异常", e);
            throw e;
        } finally {
            DbTool.closeConnection(conn, state, rs);
        }
    }

    private List<Map<?,?>> getPartionField(TableObj table, ResultSet rs2) {
        Connection conn = null;
        Statement state = null;
        ResultSet rs = null;
        String expr = null;
        String flag = null;
        List<Map<?,?>> rsList = new ArrayList<Map<?,?>>();
        try {
            conn = dbcp.getConnection();
            state = conn.createStatement();
            String pationSql = getQueryfieldsPartitionsql();
            if (pationSql.indexOf("#mysql") > 0) {
                flag = "mysql";
                pationSql = pationSql.replace("#owner#", table.getUserDbName())
                        .replace("#tableName#", table.getTableName()).replace("#mysql", "");
            } else if (pationSql.indexOf("#oracle") > 0) {
                flag = "oracle";
                pationSql = pationSql.replace("#owner#", table.getUserDbName())
                        .replace("#tableName#", table.getTableName()).replace("#oracle", "");
            } else if (pationSql.indexOf("#greenplum") > 0) {
                //GP分区特别处理，谁有好的方法可以替换掉。
                String psql="select a.attname as columnname    from pg_attribute a,pg_partition b    where a.attnum = b.paratts[0]    and b.parrelid = a.attrelid    and a.attrelid='#owner#.#tableName#'::regclass  ";
                psql = psql.replace("#owner#", table.getUserDbName())
                        .replace("#tableName#", table.getTableName());
                Statement st = conn.createStatement();
                ResultSet rset = st.executeQuery(psql);
                boolean resp = false;
                while(rset.next()){
                    while(rs2.next()){
                        if(rs2.getString(1).equalsIgnoreCase(rset.getString(1))){
                            resp = true;
                        }
                    }
                }
                DbTool.closeConnection(null, st, rset);
                if(!resp){
                    return null;
                }
                flag = "greenplum";
                pationSql = pationSql.replace("#owner#", table.getUserDbName())
                        .replace("#tableName#", table.getTableName())
                        .replace("#greenplum", "");
            } else {
                // TODO 后续添加不同类别的数据库 在此实现
                return rsList;
            }
            rs = state.executeQuery(pationSql);
            if (flag.equalsIgnoreCase("mysql")) {
                while (rs.next()) {
                    Map<String,Object> rsMap1=new HashMap<String,Object>();
                    rsMap1.put("partition_name", rs.getObject(1));
                    rsMap1.put("column_name", rs.getObject(2));
                    rsList.add(rsMap1);
                    expr = rs.getString(2);
                }
                if (StringUtils.isNotBlank(expr)) {
                    String[] exprs = expr.split(",");
                    for (String f : exprs) {
                        while(rs2.next()){
                            if (f.replace("`", "").equalsIgnoreCase(rs2.getString(1))) {
                                return rsList;
                            }
                        }
                    }
                }
            } else if (flag.equalsIgnoreCase("oracle") || flag.equalsIgnoreCase("greenplum")) {
                while (rs.next()) {
                    Map<String,Object> rsMap1=new HashMap<String,Object>();
                    rsMap1.put("partition_name", rs.getObject(1));
                    rsMap1.put("column_name", rs.getObject(2));
                    rsList.add(rsMap1);
                }
                return  rsList;
            }
        } catch (Exception e) {
            logger.error("查询表结构异常", e);
        } finally {
            DbTool.closeConnection(conn, state, rs);
        }
        return null;
    }

    /**
     * 获取字段数据
     * @param user
     * @param table
     * @param column
     * @param size
     * @return
     */
    public List<Object> getColumnData(String user,String table,String column,int size) throws Exception {
        String sql=this.getDataByFieldsSizeSQL().replaceAll("#owner#",user).replaceAll("#tableName#",table).replaceAll("#size#",size+"").replaceAll("#column#",column);
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        List<Object> rsList = new ArrayList<>();
        logger.info(sql);
        try {
            conn = dbcp.getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                Object obj=rs.getObject(1);
                if(obj instanceof Clob){
                    obj=DbTool.getClobToString((Clob)obj);
                }
                if(obj instanceof  Blob){
                    obj=DbTool.castBlobToString((Blob) obj);
                }
                rsList.add(obj);
            }
        } catch (Exception e) {
            logger.error("查询数据异常，请检查sql={}是否正确", sql, e);
            throw e;
        } finally {
            DbTool.closeConnection(conn, statement, rs);
        }
        return rsList;
    }

    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    public Connection getConn() throws SQLException {
        return dbcp.getConnection();
    }
}
