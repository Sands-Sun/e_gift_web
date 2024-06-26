package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GivingHospRelationPersonEntity;
import org.apache.ibatis.annotations.Delete;

public interface HospRelationPersonDao extends BaseMapper<GivingHospRelationPersonEntity> {

    @Delete("DELETE FROM B_PROC_HOSPITALITY_PERSON WHERE APPLICATION_ID = #{applicationId} AND TYPE = #{type}")
    void deleteByApplicationId(Long applicationId, String type);
}
