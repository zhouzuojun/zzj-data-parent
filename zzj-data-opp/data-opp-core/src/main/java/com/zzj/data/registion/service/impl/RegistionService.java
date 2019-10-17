package com.zzj.data.registion.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzj.data.registion.dao.SysCommDbMapper;
import com.zzj.data.registion.entity.SysCommDb;
import com.zzj.data.registion.service.IRegistionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


@Service
public class RegistionService implements IRegistionService {

    @Resource
    private SysCommDbMapper sysCommDbMapper;


    @Override
    public void add(SysCommDb sysCommDb) {
        sysCommDb.setId(UUID.randomUUID().toString());
        sysCommDbMapper.insertSelective(sysCommDb);
    }

    @Override
    public void update(SysCommDb sysCommDb) {
        sysCommDbMapper.updateByPrimaryKeySelective(sysCommDb);
    }

    @Override
    public void del(String id) {
        sysCommDbMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<SysCommDb> getPageList(SysCommDb sysCommDb, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<SysCommDb>(sysCommDbMapper.getList(sysCommDb));
    }

    @Override
    public SysCommDb getDetailById(String id) {
        return sysCommDbMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SysCommDb> getList() {
        return sysCommDbMapper.getList(new SysCommDb());
    }
}
