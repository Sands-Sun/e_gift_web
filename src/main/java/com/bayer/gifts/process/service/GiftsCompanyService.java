package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.entity.HospitalityRelationPersonEntity;
import com.bayer.gifts.process.form.GiftCompInfoForm;
import com.bayer.gifts.process.param.GiftsCompanySearchParam;
import com.bayer.gifts.process.param.GiftsPersonSearchParam;

import java.util.Date;
import java.util.List;

public interface GiftsCompanyService {

    List<GiftsCompanyEntity> searchGiftCompanyList(GiftsCompanySearchParam param);

    List<GiftsPersonEntity> searchGiftPersonList(GiftsPersonSearchParam param);
    List<GiftsCompanyEntity> getCompPersonByApplicationId(Long applicationId,String category,String type);

    void deleteGiftsRelationPersonByApplicationId(Long applicationId,String category,String type);
    List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(Long applicationId);
    List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(List<GiftCompInfoForm> companies, Date currentDate,
                                                            Long applicationId, Long userId, Long fileId,
                                                            Integer volume, Double unitValue, String type);


    List<HospitalityRelationPersonEntity> saveOrUpdateHospPerson(List<GiftCompInfoForm> giftCompInfoFormList,
                                                                 Date currentDate, Long applicationId, Long userId,
                                                                 Long fileId, Integer volume,
                                                                 Double unitValue, String type);
}
