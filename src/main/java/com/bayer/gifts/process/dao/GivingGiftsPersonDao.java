package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GivingGiftsPersonEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GivingGiftsPersonDao extends BaseMapper<GivingGiftsPersonEntity> {

    @Delete("DELETE FROM B_PROC_GIVING_GIFTS_PERSION WHERE APPLICATION_ID = #{applicationId}")
    void deleteByApplicationId(Long applicationId);
}
