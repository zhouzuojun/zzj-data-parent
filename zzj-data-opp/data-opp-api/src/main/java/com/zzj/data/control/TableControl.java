package com.zzj.data.control;

import com.zzj.data.params.FieldObj;
import com.zzj.data.result.ApplicationResult;
import com.zzj.data.table.service.ITableService;
import com.zzj.data.table.vo.FiledColumnVO;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("table")
public class TableControl {

    @Autowired
    private ITableService tableService;

    @GetMapping("/{dataSourceId}/{tableName}")
    public ApplicationResult<List<FieldObj>> getTable(@PathVariable("dataSourceId")String id,@PathVariable("tableName")String tableName){
        ApplicationResult<List<FieldObj>> result=new ApplicationResult<>();
        result.setData(tableService.getTableColumn(id,tableName));
        return result;
    }

    @GetMapping("/{dataSourceId}/{tableName}/{size}")
    public ApplicationResult<List<FiledColumnVO>> getColumnData(@PathVariable("dataSourceId")String id,@PathVariable("tableName")String tableName, @PathVariable("size")int size){
        ApplicationResult<List<FiledColumnVO>> result=new ApplicationResult<>();
        result.setData(tableService.getColumnData(id,tableName,size));
        return result;
    }

    @PostMapping("/{dataSourceId}/{tableName}/{size}")
    public ApplicationResult<List<LinkedHashMap<String, Object>>> preData(@PathVariable("dataSourceId")String id,@PathVariable("tableName")String tableName, @PathVariable("size")int size){
        ApplicationResult<List<LinkedHashMap<String, Object>>> result=new ApplicationResult<>();
        result.setData(tableService.preData(id, tableName, size));
        return result;
    }

    @PostMapping("export/{dataSourceId}/{tableName}/{size}")
    public void exportColumnDataExcel(@PathVariable("dataSourceId")String id,@PathVariable("tableName")String tableName, @PathVariable("size")int size){
        tableService.exportColumnData(id, tableName, size);
    }


}
