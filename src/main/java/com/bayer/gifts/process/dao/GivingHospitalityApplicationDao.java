package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.HospitalityActivityEntity;
import com.bayer.gifts.process.entity.HospitalityApplicationEntity;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GivingHospitalityApplicationDao extends BaseMapper<HospitalityApplicationEntity> {

    IPage<HospitalityApplicationEntity> queryGivingHospitalityApplicationList(
            Page<HospitalityApplicationEntity> page,
            @Param("param") GiftsApplicationParam param);
    List<HospitalityActivityEntity> queryGivingHospitalityActivityList(@Param("param") GiftsActivityParam param);
    HospitalityActivityEntity queryGivingHospitalityActivityLastOne(Long applicationId);
}
