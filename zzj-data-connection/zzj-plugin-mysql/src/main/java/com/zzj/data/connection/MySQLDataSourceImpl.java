package com.zzj.data.connection;

import com.alibaba.druid.pool.DruidDataSource;
import com.zzj.data.params.DbParams;

public class MySQLDataSourceImpl extends  MySqlDataSource{

    public MySQLDataSourceImpl(DruidDataSource dbcp, DbParams params){
        super();
        this.dbcp=dbcp;
        this.params=params;
    }
}
