package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.GivingHospActivityEntity;
import com.bayer.gifts.process.entity.GivingHospApplicationEntity;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GivingHospitalityApplicationDao extends BaseMapper<GivingHospApplicationEntity> {

    IPage<GivingHospApplicationEntity> queryGivingHospitalityApplicationList(
            Page<GivingHospApplicationEntity> page,
            @Param("param") GiftsApplicationParam param);
    List<GivingHospActivityEntity> queryGivingHospitalityActivityList(@Param("param") GiftsActivityParam param);
    GivingHospActivityEntity queryGivingHospitalityActivityLastOne(Long applicationId);
}
