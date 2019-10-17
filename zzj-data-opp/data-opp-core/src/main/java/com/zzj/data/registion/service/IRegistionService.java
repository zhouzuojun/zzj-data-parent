package com.zzj.data.registion.service;

import com.github.pagehelper.PageInfo;
import com.zzj.data.registion.entity.SysCommDb;

import java.util.List;

/**
 * 资源注册
 */
public interface IRegistionService {

    /**
     * 添加新数据源
     * @param sysCommDb
     */
    public void add(SysCommDb sysCommDb);

    /**
     * 修改数据源信息
     * @param sysCommDb
     */
    public void update(SysCommDb sysCommDb);

    /**
     * 删除数据
     * @param id
     */
    public void del(String id);

    /**
     * 分页获取数据源信息
     * @param sysCommDb
     * @return
     */
    public PageInfo<SysCommDb> getPageList(SysCommDb sysCommDb, Integer pageNum, Integer pageSize);

    /**
     * 根据id获取数据源详细信息
     * @param id
     * @return
     */
    public SysCommDb getDetailById(String id);
    List<SysCommDb> getList();



}
