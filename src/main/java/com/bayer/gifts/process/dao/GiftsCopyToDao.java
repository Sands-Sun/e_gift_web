package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsCopyToEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GiftsCopyToDao extends BaseMapper<GiftsCopyToEntity> {

    void deleteByApplicationId(Long applicationId);

    List<GiftsCopyToEntity> queryGiftsCopyToList(Long applicationId, String type);
}
