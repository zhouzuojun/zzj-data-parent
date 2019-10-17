package com.zzj.data.params;

import java.net.URL;

/**
 * 数据库连接参数
 * @author  zhouzj
 */
public class DbParams {

    private String dsId;
    private String url;

    private String type;

    private String user;

    private String password;

    private int maxActive;

    private String datasourceImplClass;

    private URL[] driverUrls;

    private String driverClassName;

    private String queryDbTimeSql;

    private boolean isCheckIpPort;

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public String getDatasourceImplClass() {
        return datasourceImplClass;
    }

    public void setDatasourceImplClass(String datasourceImplClass) {
        this.datasourceImplClass = datasourceImplClass;
    }

    public URL[] getDriverUrls() {
        return driverUrls;
    }

    public void setDriverUrls(URL[] driverUrls) {
        this.driverUrls = driverUrls;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getQueryDbTimeSql() {
        return queryDbTimeSql;
    }

    public void setQueryDbTimeSql(String queryDbTimeSql) {
        this.queryDbTimeSql = queryDbTimeSql;
    }

    public boolean isCheckIpPort() {
        return isCheckIpPort;
    }

    public void setCheckIpPort(boolean checkIpPort) {
        isCheckIpPort = checkIpPort;
    }
}
