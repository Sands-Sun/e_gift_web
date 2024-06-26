package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.param.GiftsCompanySearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftsCompanyDao extends BaseMapper<GiftsCompanyEntity> {
    List<GiftsCompanyEntity> queryFuzzyCompanyList(@Param("param") GiftsCompanySearchParam param);

    List<GiftsCompanyEntity> selectCompPersonByApplicationId(Long applicationId,String category, String type);
    List<GiftsCompanyEntity> selectHisComPersonByApplicationId(Long applicationId);
}
