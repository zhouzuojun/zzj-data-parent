package com.zzj.data.datasource.service;

import com.zzj.data.datasource.entity.TableEntity;

import java.util.Map;

public interface IDataSourceService {

    /**
     * 根據数据源id获取到该数据库所有的表
     * @param entity
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Map<String,Object> getAllTable(TableEntity entity, int pageNum, int pageSize);
}
