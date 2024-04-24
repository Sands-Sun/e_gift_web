package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.GivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;

import java.util.Date;

public interface GivingGiftsService {

    void updateGivingGifts(GivingGiftsForm form);

    void saveGivingGifts(GivingGiftsForm form);

    void deleteDraftGivingGifts(Long applicationId);

    GivingGiftsApplicationEntity getGivingGiftsByApplicationId(Long applicationId);

    Pagination<GivingGiftsApplicationEntity> getGivingGiftsApplicationList(GiftsApplicationParam param);
}
