package com.zzj.data.connection;


import com.zzj.data.core.DbDatasource;

public abstract class OracleDatasource extends DbDatasource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 预览数据的SQL模板
     */
    private static final String PRE_QUERY_DATA_SQL = "SELECT R.* FROM #owner#.#tableName# R WHERE rownum <= #size#";


    /**
     * 查询表信息列表的SQL模板
     */
    private static final String QUERY_TABLES_SQL = " SELECT CONCAT(O.OWNER,O.OBJECT_NAME) AS OWNERANDOBJECTNAME ,O.OWNER AS OWNER,O.OBJECT_NAME AS OBJECTNAME,O.OBJECT_TYPE AS OBJECTTYPE,C.COMMENTS AS COMMENTS FROM ALL_OBJECTS O LEFT JOIN ALL_TAB_COMMENTS C ON O.OBJECT_NAME = C.TABLE_NAME AND O.OWNER = C.OWNER WHERE O.OBJECT_TYPE IN ('TABLE','VIEW') AND O.OWNER NOT IN ('SYSTEM','XDB','SYS','TSMSYS','MDSYS','SYSMAN','EXFSYS','WMSYS','ORDSYS','OUTLN','DBSNMP','SCOTT','PUBLIC','DMSYS','CTXSYS','OLAPSYS','ORDPLUGINS') AND O.OWNER NOT LIKE '%$%' AND O.OWNER not like '%SYS' AND O.OBJECT_NAME not like 'SYS_%_TEMP'  AND O.OBJECT_NAME NOT LIKE '%$%' order by O.OWNER ";

    /**
     * 查询表的字段信息列表的SQL模板
     */
    private static final String QUERY_FIELDS_SQL = "select atc.column_name,atc.data_type,NVL(atc.DATA_PRECISION, atc.data_length) AS data_length, acc.comments, (select 'Y' from all_cons_columns accs, all_constraints ac where accs.owner = atc.owner and ac.owner = accs.owner and accs.TABLE_NAME = atc.table_name and ac.CONSTRAINT_TYPE = 'P' and accs.CONSTRAINT_NAME = ac.CONSTRAINT_NAME and accs.column_name = atc.column_name) AS dbKeyFlg,(select 'Y' from all_ind_columns t where t.column_name = atc.column_name and t.table_name = atc.table_name and t.index_owner = atc.owner and t.table_owner = atc.OWNER and rownum = 1 ) colIndex from all_tab_columns atc, all_col_comments acc where upper(atc.owner) = upper('#owner#') and atc.owner = acc.owner and upper(acc.table_name) = upper('#tableName#') and atc.table_name = acc.table_name and atc.column_name = acc.column_name";

	/**
	 * 获取字段值
	 */
	private static final String DATA_BY_FIELDS_SIZE_SQL = "SELECT #column# FROM #owner##tableName#  t WHERE T.#column# IS NOT NULL AND ROWNUM <= #size#";

	private static final String QUERY_FIELDS_PARTITION_SQL ="select p.partition_name as partition_name,c.column_name as column_name from USER_TAB_PARTITIONS p,all_PART_KEY_COLUMNS c  where c.name=upper('#tableName#') and c.owner=upper('#owner#') and p.table_name = upper('#tableName#')#oracle";

	@Override
    public String getPreQueryDataSQL() {
        return PRE_QUERY_DATA_SQL;
    }

    @Override
    public String getQueryTablesSQL() {
        return QUERY_TABLES_SQL;
    }

    @Override
    public String getQueryFieldsSQL() {
        return QUERY_FIELDS_SQL;
    }

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
        StringBuffer dataSql = new StringBuffer();
        dataSql.append(" select * from ");
        dataSql.append(" (select AAAAA.*,rownum row_num from ");
        dataSql.append("  (  ");
        dataSql.append(sbf);
        dataSql.append("  ) AAAAA  ");
        dataSql.append("  ) BBBBB  ");
        dataSql.append("  where BBBBB.row_num > ").append(((pageNum - 1) * pageSize));
        dataSql.append(" and BBBBB.row_num <=").append((pageNum * pageSize));
        return dataSql.toString();
    }
}
