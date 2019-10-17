package com.zzj.data.params;

public class TableParams {

    private String ownerAndObjectName;

    private String userDbName;

    private String schemaname;

    private String tableName;

    private String type;

    private String comment;

    public String getOwnerAndObjectName() {
        return ownerAndObjectName;
    }

    public void setOwnerAndObjectName(String ownerAndObjectName) {
        this.ownerAndObjectName = ownerAndObjectName;
    }

    public String getUserDbName() {
        return userDbName;
    }

    public void setUserDbName(String userDbName) {
        this.userDbName = userDbName;
    }

    public String getSchemaname() {
        return schemaname;
    }

    public void setSchemaname(String schemaname) {
        this.schemaname = schemaname;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
