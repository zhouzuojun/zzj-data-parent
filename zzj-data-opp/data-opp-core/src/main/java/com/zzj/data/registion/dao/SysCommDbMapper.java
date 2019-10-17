package com.zzj.data.registion.dao;

import com.zzj.data.registion.entity.SysCommDb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysCommDbMapper {
    int deleteByPrimaryKey(@Param("id")String id);


    int insertSelective(SysCommDb record);

    SysCommDb selectByPrimaryKey(@Param("id")String id);

    int updateByPrimaryKeySelective(SysCommDb record);

    List<SysCommDb> getList(SysCommDb record);
}
