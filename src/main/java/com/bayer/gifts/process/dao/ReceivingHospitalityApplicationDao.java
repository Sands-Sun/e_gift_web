package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.ReceivingGiftsApplicationEntity;
import com.bayer.gifts.process.entity.ReceivingHospActivityEntity;
import com.bayer.gifts.process.entity.ReceivingHospApplicationEntity;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReceivingHospitalityApplicationDao extends BaseMapper<ReceivingHospApplicationEntity> {

    IPage<ReceivingHospApplicationEntity> queryReceivingHospitalityApplicationList(Page<ReceivingGiftsApplicationEntity> page,
                                                                                   @Param("param") GiftsApplicationParam param);

    List<ReceivingHospActivityEntity> queryReceivingHospitalityActivityList(@Param("param") GiftsActivityParam param);
}
