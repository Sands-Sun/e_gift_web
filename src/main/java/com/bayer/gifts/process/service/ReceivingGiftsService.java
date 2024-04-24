package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.ReceivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.ReceivingGiftsApplicationEntity;
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.form.ReceivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;

public interface ReceivingGiftsService {

    void deleteDraftReceivingGifts(Long applicationId);
    void updateReceivingGifts(ReceivingGiftsForm form);

    void cancelReceivingGifts(ReceivingGiftsForm giftsForm);
    void saveReceivingGifts(ReceivingGiftsForm form);

    ReceivingGiftsApplicationEntity getReceivingGiftsByApplicationId(Long applicationId);

    Pagination<ReceivingGiftsApplicationEntity> getReceivingGiftsApplicationList(GiftsApplicationParam param);

}
