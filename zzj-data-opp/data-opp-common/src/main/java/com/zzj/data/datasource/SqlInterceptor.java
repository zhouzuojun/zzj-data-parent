package com.zzj.data.datasource;


import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

@Intercepts({@Signature(type = Executor.class,
        method = "query",
        args = {MappedStatement.class,
                Object.class,
                RowBounds.class, ResultHandler.class}
),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SqlInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 记录完整sql语句日志
        logCompleteSql(invocation);
        // 返回到mybatis执行路径
        Object ret = invocation.proceed();
        // 记录更新类sql语句执行后影响行数日志
        logEffectedRows(ret, invocation);
        return ret;
    }

    /**
     * 记录完整sql语句日志     * @param invocation     * @throws Throwable
     */
    public void logCompleteSql(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameterObject = args[1];

        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        Configuration configuration = mappedStatement.getConfiguration();
        String originalSql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.size() == 0) {
            logger.info("==> Complete SQL: " + originalSql);
            return;
        }
        StringBuffer buf = new StringBuffer();
        MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
        StringTokenizer sqlTokenizer = new StringTokenizer(originalSql, "?");
        int tokenIndex = 0;
        while (sqlTokenizer.hasMoreTokens()) {
            buf.append(sqlTokenizer.nextToken());
            Object value = null;
            if (tokenIndex < parameterMappings.size()) {
                ParameterMapping parameterMapping = parameterMappings.get(tokenIndex);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                }
            }
            if (value != null) {
                appendParameter(value, buf);
            } else if (sqlTokenizer.hasMoreTokens()) {
                append("NULL", buf);
            }
            tokenIndex++;
        }
        logger.info("==> Complete SQL: \r\t" + buf.toString());
    }

    /**
     * 记录更新类sql语句执行后影响行数日志     * @param retObj     * @param statementLog     * @param methodName
     */
    public void logEffectedRows(Object retObj, Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];

        if (retObj != null && (retObj instanceof Integer) && "update".equals(invocation.getMethod().getName())) {
            logger.info("==> Effected rows: " + retObj);
        }
    }

    public void appendParameter(Object param, Appendable appender) {
        if (param == null) {
            append("NULL", appender);
            return;
        }
        if (param instanceof Number //
                || param instanceof Boolean) {
            append(param.toString(), appender);
            return;
        }
        if (param instanceof String) {
            String text = (String) param;
            if ((text == null) || (text.length() == 0)) {
                append("NULL", appender);
            } else {
                append("'", appender);
                append(text.replaceAll("'", "''"), appender);
                append("'", appender);
            }
            return;
        }
        if (param instanceof Date) {
            append((Date) param, appender);
            return;
        }
        if (param instanceof InputStream) {
            append("'<InputStream>", appender);
            return;
        }
        if (param instanceof Reader) {
            append("'<Reader>", appender);
            return;
        }
        if (param instanceof Blob) {
            append("'<Blob>", appender);
            return;
        }
        if (param instanceof NClob) {
            append("'<NClob>", appender);
            return;
        }
        if (param instanceof Clob) {
            append("'<Clob>", appender);
            return;
        }
        append("'" + param.getClass().getName() + "'", appender);
    }

    public void append(char value, Appendable appender) {
        try {
            appender.append(value);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    public void append(int value, Appendable appender) {
        append(Integer.toString(value), appender);
    }

    public void append(Date date, Appendable appender) {
        SimpleDateFormat dateFormat;
        if (date instanceof java.sql.Timestamp) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        append("'" + dateFormat.format(date) + "'", appender);
    }

    public void append(long value, Appendable appender) {
        append(Long.toString(value), appender);
    }

    public void append(String text, Appendable appender) {
        try {
            appender.append(text);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
