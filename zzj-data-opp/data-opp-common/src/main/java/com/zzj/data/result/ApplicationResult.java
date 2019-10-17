package com.zzj.data.result;

import java.io.Serializable;

public class ApplicationResult<T> implements Serializable {

    private Integer state=200;

    private T data;

    private String msg="success";

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
