package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.vo.TaskInstanceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftsProcessDao {

    IPage<TaskInstanceVo> queryTaskList(Page<TaskInstanceVo> page, @Param("param") GiftsTaskParam param);
    Long queryTaskCount(@Param("param") GiftsTaskParam param);

    List<TaskInstanceVo> queryTaskByProcessInsId(String processInsId, String requestType);


}
