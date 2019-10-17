package com.zzj.data.control;

import com.zzj.data.datasource.entity.TableEntity;
import com.zzj.data.datasource.service.IDataSourceService;
import com.zzj.data.result.ApplicationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/source")
public class DataSourceControl {

    @Autowired
    private IDataSourceService dataSourceService;
    @PostMapping("/{pageNum}/{pageSize}")
    public ApplicationResult<Map<String, Object>> getPageList(@RequestBody  TableEntity tableEntity, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        ApplicationResult<Map<String, Object>> result = new ApplicationResult<>();
        result.setData(dataSourceService.getAllTable(tableEntity,pageNum,pageSize));
        return result;
    }
}
