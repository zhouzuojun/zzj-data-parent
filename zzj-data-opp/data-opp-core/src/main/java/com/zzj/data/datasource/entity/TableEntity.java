package com.zzj.data.datasource.entity;

import java.io.Serializable;

public class TableEntity implements Serializable {

    private String dbId;

    private String tableName;

    private  Integer type;

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
