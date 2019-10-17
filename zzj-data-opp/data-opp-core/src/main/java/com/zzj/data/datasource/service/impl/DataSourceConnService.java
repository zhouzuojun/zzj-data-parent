package com.zzj.data.datasource.service.impl;

import com.zzj.data.core.DbBuilder;
import com.zzj.data.core.DbDatasource;
import com.zzj.data.datasource.service.IDataSourceConnService;
import com.zzj.data.error.DataException;
import com.zzj.data.params.DbParams;
import com.zzj.data.util.DbTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DataSourceConnService implements IDataSourceConnService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Override
    public String testConnection(DbParams dbParams) {
        Connection connection=null;

        try {
            DbDatasource dbSource=DbBuilder.build(dbParams);
            connection=dbSource.getConn();
            return "测试成功";
        } catch (Exception e) {
            logger.error("数据库测试异常",e);
            throw  new DataException("连接失败:",e);
        }finally {
            DbTool.closeConnection(connection,null,null);
        }
    }
}
