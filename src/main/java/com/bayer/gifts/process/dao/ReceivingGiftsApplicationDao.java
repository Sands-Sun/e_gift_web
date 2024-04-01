package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.ReceivingGiftsApplicationEntity;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReceivingGiftsApplicationDao extends BaseMapper<ReceivingGiftsApplicationEntity> {


    IPage<ReceivingGiftsApplicationEntity> queryReceivingGiftsApplicationList(
            Page<ReceivingGiftsApplicationEntity> page,
            @Param("param") GiftsApplicationParam param);
}
