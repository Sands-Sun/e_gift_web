package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftCompanySearchParam;
import com.bayer.gifts.process.param.GiftPersonSearchParam;

import java.util.Date;
import java.util.List;

public interface GiftsCompanyService {

    List<GiftsCompanyEntity> searchGiftCompanyList(GiftCompanySearchParam param);
    List<GiftsPersonEntity> searchGiftPersonList(GiftPersonSearchParam param);

    void deleteGiftsRelationPersonByApplicationId(Long applicationId);
    List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(Long applicationId);
    List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(Date currentDate, Long applicationId, Long userId,String personTitle,
                                                            String company, List<String> personList,Double unitValue);
}
