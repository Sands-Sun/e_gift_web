package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.param.GiftsPersonSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftsPersonDao extends BaseMapper<GiftsPersonEntity> {
    List<GiftsPersonEntity> queryFuzzyPersonList(@Param("param") GiftsPersonSearchParam param);
    List<GiftsRelationPersonEntity> queryGiftsRelationPersonList(String type, Long applicationId);
}
