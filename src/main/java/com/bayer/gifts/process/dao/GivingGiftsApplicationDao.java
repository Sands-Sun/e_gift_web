package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.GivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GivingGiftsApplicationDao extends BaseMapper<GivingGiftsApplicationEntity> {

    IPage<GivingGiftsApplicationEntity> queryGivingGiftsApplicationList(
            Page<GivingGiftsApplicationEntity> page,
            @Param("param") GiftsApplicationParam param);

    List<GivingGiftsActivityEntity> queryGivingGiftsActivityList(@Param("param") GiftsActivityParam param);

    GivingGiftsActivityEntity queryGivingGiftsActivityLastOne(Long applicationId);
}
