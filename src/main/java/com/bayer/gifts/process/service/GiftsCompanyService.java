package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.form.CompanyInfoForm;
import com.bayer.gifts.process.param.GiftsCompanySearchParam;
import com.bayer.gifts.process.param.GiftsPersonSearchParam;

import java.util.Date;
import java.util.List;

public interface GiftsCompanyService {

    List<GiftsCompanyEntity> searchGiftCompanyList(GiftsCompanySearchParam param);

    List<GiftsCompanyEntity> getCompPersonByApplicationId(Long applicationId,String type);
    List<GiftsPersonEntity> searchGiftPersonList(GiftsPersonSearchParam param);

    void deleteGiftsRelationPersonByApplicationId(Long applicationId,String type);
    List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(Long applicationId);
    List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(List<CompanyInfoForm> companies, Date currentDate,
                                                            Long applicationId, Long userId, Long fileId,
                                                            Integer volume, Double unitValue,String type);
}
