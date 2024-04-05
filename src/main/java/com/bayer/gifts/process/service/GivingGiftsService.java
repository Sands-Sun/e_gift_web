package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.GivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;

import java.util.Date;

public interface GivingGiftsService {

    void updateDraftGivingGifts(GivingGiftsForm form);

    void saveGivingGifts(GivingGiftsForm form);

    GivingGiftsApplicationEntity getGivingGiftsByApplicationId(Long applicationId);

    Pagination<GivingGiftsApplicationEntity> getGivingGiftsApplicationList(GiftsApplicationParam param);
}
