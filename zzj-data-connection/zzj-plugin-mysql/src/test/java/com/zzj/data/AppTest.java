package com.zzj.data;

import static org.junit.Assert.assertTrue;

import com.zzj.data.core.DbBuilder;
import com.zzj.data.core.DbDatasource;
import com.zzj.data.params.DbParams;
import com.zzj.data.params.FieldObj;
import com.zzj.data.params.TableObj;
import com.zzj.data.params.TableParams;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        DbParams dbParams=new DbParams();
        dbParams.setType("102");
        dbParams.setMaxActive(1);
        dbParams.setUrl("jdbc:mysql://localhost:3306/zzj_data?characterEncoding=UTF-8");
        dbParams.setUser("root");
        dbParams.setPassword("admin");
        DbDatasource dataSource;
        try {
            dataSource= DbBuilder.build(dbParams);
            /*TableParams table=new TableParams();
            table.setSchemaname("zzj_data");
            table.setTableName("sys_comm_db");
            int size=10;
            List<LinkedHashMap<String, Object>> dataList= dataSource.preQueryData(table,size);*/
            TableObj tableObj=new TableObj();
            tableObj.setUserDbName("zzj_data");
            tableObj.setTableName("sys_comm_db");
            List<FieldObj> fieldObjs=dataSource.queryFieldList(tableObj);
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
