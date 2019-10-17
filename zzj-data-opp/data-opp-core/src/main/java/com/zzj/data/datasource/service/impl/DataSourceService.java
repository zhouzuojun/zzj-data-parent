package com.zzj.data.datasource.service.impl;

import com.zzj.data.core.DbBuilder;
import com.zzj.data.core.DbDatasource;
import com.zzj.data.datasource.entity.TableEntity;
import com.zzj.data.datasource.service.IDataSourceService;
import com.zzj.data.error.DataException;
import com.zzj.data.params.DbParams;
import com.zzj.data.registion.entity.SysCommDb;
import com.zzj.data.registion.service.IRegistionService;
import com.zzj.data.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataSourceService implements IDataSourceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IRegistionService registionService;

    @Override
    public Map<String,Object> getAllTable(TableEntity entity, int pageNum, int pageSize) {
        Map<String,Object> map=new HashMap<>();
        SysCommDb db=registionService.getDetailById(entity.getDbId());
        try {
            DbDatasource dbDatasource= DbBuilder.build(getParams(db));
            map.put("list",dbDatasource.queryTableList(getOwner(db),entity.getTableName(),entity.getType(),pageNum,pageSize));
            map.put("total",dbDatasource.queryTableListCount(db.getDbName(),entity.getTableName(),entity.getType(),pageNum,pageSize));
        } catch (Exception e) {
            logger.error("数据源异常：",e);
            throw new DataException("执行数据操作时出现异常：",e);
        }

        return map;
    }
    private String getOwner(SysCommDb db) {
        String owner = null;
        switch (Integer.parseInt(db.getType())) {
            case Constants.DB_SOURCE.MYSQL_TYPE:
                owner = db.getDbName();
                break;
            case Constants.DB_SOURCE.ORACLE_TYPE:
                owner = db.getAccount();
                break;
            default:
                break;

        }
        return owner;
    }
    private DbParams getParams(SysCommDb db) {
        String url = null;
        DbParams params=new DbParams();
        switch (Integer.parseInt(db.getType())) {
            case Constants.DB_SOURCE.MYSQL_TYPE:
                url = "jdbc:mysql://"+db.getHost()+":"+db.getPort()+"/"+db.getDbName()+"?characterEncoding=UTF-8";
                break;
            case Constants.DB_SOURCE.ORACLE_TYPE:
                url="jdbc:oracle:thin:@"+db.getHost()+":"+db.getPort()+":"+db.getDbName()+"";
                break;
            default:
                break;

        }
        params.setUser(db.getAccount());
        params.setPassword(db.getPassword());
        params.setUrl(url);
        params.setMaxActive(1);
        params.setType(db.getType());
        return params;
    }
}
