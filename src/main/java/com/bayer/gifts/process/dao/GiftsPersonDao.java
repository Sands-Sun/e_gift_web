package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.param.GiftPersonSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftsPersonDao extends BaseMapper<GiftsPersonEntity> {

    List<GiftsPersonEntity> queryFuzzyPersonList(@Param("param") GiftPersonSearchParam param);
}
