package com.zzj.data.datasource.service;

import com.zzj.data.params.DbParams;

public interface IDataSourceConnService {

    /**
     * 测试连接是否可用
     * @param dbParams
     * @return
     */
    String testConnection(DbParams dbParams);
}
