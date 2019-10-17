package com.zzj.data.table.service;

import com.zzj.data.params.FieldObj;
import com.zzj.data.params.TableObj;
import com.zzj.data.table.vo.FiledColumnVO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ITableService {


    /**
     * 根据数据源和表名称获取该表所有的字段信息
     * @param dataSourceId
     * @param tableName
     * @return
     */
    public List<FieldObj>  getTableColumn(String dataSourceId, String tableName);

    /**
     * 根据数据源和表，每个字段获取对应条数数据
     * @param dataSourceId
     * @param tableName
     * @param size
     * @return
     */
    public List<FiledColumnVO> getColumnData(String dataSourceId, String tableName, int size);


    /**
     * 预览数据
     * @param dataSourceId
     * @param tableName
     * @param size
     * @return
     */
    public List<LinkedHashMap<String, Object>> preData(String dataSourceId, String tableName, int size);

    /**
     * 导出excel
     * @param dataSourceId
     * @param tableName
     * @param size
     */
    public void exportColumnData(String dataSourceId, String tableName, int size);
}
