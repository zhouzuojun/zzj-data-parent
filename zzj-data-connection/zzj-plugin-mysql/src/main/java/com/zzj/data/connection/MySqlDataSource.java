package com.zzj.data.connection;

import com.zzj.data.core.DbDatasource;

public abstract class MySqlDataSource extends DbDatasource {
    private static final long serialVersionUID = 1L;

    private static final String PRE_QUERY_DATA_SQL = "SELECT * FROM #owner#.#tableName# LIMIT #size#";

    private static final String QUERY_TABLES_SQL = " select CONCAT(V.TABLE_SCHEMA,V.TABLE_NAME) AS OWNERANDOBJECTNAME,V.TABLE_SCHEMA AS OWNER,V.TABLE_NAME AS OBJECTNAME,'VIEW' AS OBJECTTYPE,null as COMMENT from information_schema.VIEWS V WHERE V.TABLE_SCHEMA !='sakila' UNION ALL select CONCAT(T.TABLE_SCHEMA,T.TABLE_NAME) AS OWNERANDOBJECTNAME,T.TABLE_SCHEMA AS OWNER,T.TABLE_NAME AS OBJECTNAME,'TABLE' AS OBJECTTYPE,T.TABLE_COMMENT from information_schema.TABLES T where T.TABLE_SCHEMA not in('information_schema','mysql','performance_schema','sakila','test','world') and T.TABLE_TYPE='BASE TABLE' ";

    private static final String QUERY_FIELDS_SQL = "SELECT t.COLUMN_NAME COLUMN_NAME, UPPER(t.DATA_TYPE) DATA_TYPE, CASE INSTR(t.COLUMN_TYPE, '(') WHEN 0 THEN 0 ELSE SUBSTRING_INDEX( SUBSTRING_INDEX(t.COLUMN_TYPE, '(' ,- 1), ')', 1 ) END DATA_LENGTH ,T.COLUMN_COMMENT AS COMMENTS, CASE t.COLUMN_KEY WHEN 'PRI' THEN 'Y' ELSE null end as DBKEYFILG,(select 'Y' from information_schema.STATISTICS s where UPPER(s.TABLE_SCHEMA)=t.TABLE_SCHEMA and UPPER(t.TABLE_NAME)=t.TABLE_NAME and s.COLUMN_NAME=t.COLUMN_NAME limit 0,1) COLINDEX FROM information_schema.`COLUMNS` t WHERE UPPER(t.TABLE_SCHEMA)=UPPER('#owner#') AND UPPER(t.TABLE_NAME) = UPPER('#tableName#')";

    private static final String DATA_BY_FIELDS_SIZE_SQL = "SELECT #column# FROM #owner##tableName#  t WHERE T.#column# IS NOT NULL LIMIT #size#";

    /**
     * 查询表的字段分区信息SQL模板
     */
    private static final String QUERY_FIELDS_PARTITION_SQL = "select PARTITION_NAME,PARTITION_EXPRESSION from  information_schema.PARTITIONS WHERE UPPER(TABLE_SCHEMA)=UPPER('#owner#') AND UPPER(TABLE_NAME)=UPPER('#tableName#') #mysql";//参数#mysql为了后面作判断

    private static final String QUERY_TABLE_SQL = "select AAAAAA.* from (#sql#) AAAAAA  limit #pageNum#,#pageSize#";

    /**
     * 获取预览数据的sql模板
     *
     * @return
     */
    @Override
    public String getPreQueryDataSQL() {
        return PRE_QUERY_DATA_SQL;
    }

    /**
     * 查询表信息列表的SQL模板
     *
     * @return
     */
    @Override
    public String getQueryTablesSQL() {
        return QUERY_TABLES_SQL;
    }

    /**
     * 查询字段信息列表的SQL模板
     *
     * @return
     */
    @Override
    public String getQueryFieldsSQL() {
        return QUERY_FIELDS_SQL;
    }

    /**
     * 获取字段查询SQL模板
     *
     * @return
     */
    @Override
    public String getDataByFieldsSizeSQL() {
        return DATA_BY_FIELDS_SIZE_SQL;
    }

    @Override
    public String getQueryfieldsPartitionsql() {
        return QUERY_FIELDS_PARTITION_SQL;
    }

    @Override
    public String getQueryTableByPageSQL(StringBuffer sbf, int pageNum, int pageSize) {
        return QUERY_TABLE_SQL.replaceAll("#sql#", sbf.toString()).replaceAll("#pageNum#", ((pageNum - 1) * pageSize) + "").replaceAll("#pageSize#", pageSize + "");
    }
}
