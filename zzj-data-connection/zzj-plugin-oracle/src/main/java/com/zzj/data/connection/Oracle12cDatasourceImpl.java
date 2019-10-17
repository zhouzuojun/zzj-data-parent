package com.zzj.data.connection;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzj.data.params.DbParams;

public class Oracle12cDatasourceImpl extends OracleDatasource {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Oracle12cDatasourceImpl(DruidDataSource dbcp, DbParams dsParams) {
		super();
		this.params = dsParams;
		this.dbcp = dbcp;
	}


}
