package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GiftCompInfoForm;
import com.bayer.gifts.process.param.GiftsCompanySearchParam;
import com.bayer.gifts.process.param.GiftsPersonSearchParam;

import java.io.File;
import java.util.Date;
import java.util.List;

public interface GiftsCompanyService {

    List<GiftsCompanyEntity> mergeFromFileAttach(File file, String type, String companyCode);
    List<GiftsCompanyEntity> searchGiftCompanyList(GiftsCompanySearchParam param);

    List<GiftsPersonEntity> searchGiftPersonList(GiftsPersonSearchParam param);
    List<GiftsCompanyEntity> getCompPersonByApplicationId(Long applicationId,String category,String type);

    List<GiftsCompanyEntity> getHisComPersonByApplicationId(Long applicationId);

    void deleteGiftsRelationPersonByApplicationId(Long applicationId,String category,String type);
    List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(String type,Long applicationId);
    List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(List<GiftCompInfoForm> companies, Date currentDate,
                                                            Long applicationId, Long userId, Long fileId, String type);

    List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(Long applicationId,Date currentDate,String type,
                                                            List<GiftsPersonEntity> giftsPersonList);
    List<GivingHospRelationPersonEntity> saveOrUpdateHospPerson(List<GiftCompInfoForm> giftCompInfoFormList,
                                                                UserExtensionEntity user,
                                                                Date currentDate, Long applicationId,
                                                                Double expensePerHead,
                                                                Long fileId, String type);

    List<GivingHospRelationPersonEntity> saveOrUpdateHospPerson(Long applicationId, Date currentDate, String type,
                                                                Double expensePerHead,
                                                                List<GiftsPersonEntity> giftsPersonList);
}
