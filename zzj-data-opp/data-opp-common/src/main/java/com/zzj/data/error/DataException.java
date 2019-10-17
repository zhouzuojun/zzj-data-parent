package com.zzj.data.error;

public class DataException extends  RuntimeException  {
    private static final long serialVersionUID = 1L;
    private Integer code = 500;

    private boolean propertiesKey = false;


    public Integer getCode() {
        return code;
    }



    public void setCode(Integer code) {
        this.code = code;
    }



    public boolean isPropertiesKey() {
        return propertiesKey;
    }



    public void setPropertiesKey(boolean propertiesKey) {
        this.propertiesKey = propertiesKey;
    }



    public DataException(String msg) {
        super(msg);
    }



    public DataException(Throwable throwable) {
        super(throwable);
    }



    public DataException(String msg, Throwable throwable) {
        super(msg, throwable);
    }



    public DataException(Integer code, String msg) {
        this(code, msg, true);

    }



    public DataException(Integer code, String msg, boolean key) {
        super(msg);
        this.code = code;
        this.propertiesKey = key;
    }



    public DataException(Integer code, String msg, Throwable throwable) {
        this(code, msg, throwable, true);
    }



    public DataException(Integer code, String msg, Throwable throwable, boolean key) {
        super(msg, throwable);
        this.code = code;
        this.propertiesKey = key;
    }
}
