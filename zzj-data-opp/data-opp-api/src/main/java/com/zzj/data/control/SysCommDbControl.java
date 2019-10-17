package com.zzj.data.control;

import com.github.pagehelper.PageInfo;
import com.zzj.data.registion.entity.SysCommDb;
import com.zzj.data.registion.service.IRegistionService;
import com.zzj.data.result.ApplicationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasource")
public class SysCommDbControl {

    @Autowired
    private IRegistionService rgistionService;


    @PostMapping("")
    public ApplicationResult<?> add(@RequestBody SysCommDb sysCommDb){
        ApplicationResult<?> result=new ApplicationResult<>();
        rgistionService.add(sysCommDb);
        return result;
    }

    @PutMapping("")
    public ApplicationResult<?> update(@RequestBody SysCommDb sysCommDb){
        ApplicationResult<?> result=new ApplicationResult<>();
        rgistionService.update(sysCommDb);
        return result;
    }

    @DeleteMapping("/{id}")
    public ApplicationResult<String> del(@PathVariable("id")String id){
        ApplicationResult<String > result=new ApplicationResult<>();
        rgistionService.del(id);
        result.setData("");
        return result;
    }

    @PostMapping("/{pageNum}/{pageSize}")
    public ApplicationResult<PageInfo<SysCommDb>> getPage(@RequestBody SysCommDb sysCommDb, @PathVariable("pageNum")Integer pageNum, @PathVariable("pageSize")Integer pageSize){
        ApplicationResult<PageInfo<SysCommDb>> result=new ApplicationResult<>();
        result.setData(rgistionService.getPageList(sysCommDb,pageNum,pageSize));
        return result;
    }

    @GetMapping("/{id}")
    public ApplicationResult<SysCommDb> getDetail(@PathVariable("id")String id){
        ApplicationResult<SysCommDb> result=new ApplicationResult<>();
        result.setData(rgistionService.getDetailById(id));
        return result;

    }


    @GetMapping("/list")
    public ApplicationResult<List<SysCommDb>> getList(){
        ApplicationResult<List<SysCommDb>> result=new ApplicationResult<>();
        result.setData(rgistionService.getList());
        return result;

    }

}
