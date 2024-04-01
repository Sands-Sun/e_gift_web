package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsCopyToEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GiftsCopyToDao extends BaseMapper<GiftsCopyToEntity> {

    @Delete("DELETE FROM B_PROC_COMMON_MSG_COPYTO WHERE APPLICATION_ID = #{applicationId}")
    void deleteByApplicationId(Long applicationId);
}
