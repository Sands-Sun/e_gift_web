package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GiftsRelationPersonDao extends BaseMapper<GiftsRelationPersonEntity> {

    @Delete("DELETE FROM B_PROC_GIVING_GIFTS_PERSION WHERE APPLICATION_ID = #{applicationId} AND TYPE = #{type}")
    void deleteByApplicationId(Long applicationId, String type);
}
