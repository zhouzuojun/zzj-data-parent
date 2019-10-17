package com.zzj.data.control;

import com.zzj.data.datasource.service.IDataSourceConnService;
import com.zzj.data.params.DbParams;
import com.zzj.data.registion.entity.SysCommDb;
import com.zzj.data.result.ApplicationResult;
import com.zzj.data.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/connection")
public class DataSourceConnControl {
    @Autowired
    private IDataSourceConnService dataSourceConnService;

    @PostMapping
    public ApplicationResult<String> test(@RequestBody SysCommDb db) {
        ApplicationResult<String> result = new ApplicationResult<>();
        result.setData(dataSourceConnService.testConnection(getParams(db)));
        return result;
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
