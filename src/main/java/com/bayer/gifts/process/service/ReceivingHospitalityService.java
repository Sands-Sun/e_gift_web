package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.ReceivingHospApplicationEntity;
import com.bayer.gifts.process.param.GiftsApplicationParam;

public interface ReceivingHospitalityService {

    ReceivingHospApplicationEntity getReceivingHospitalityHistoryByApplicationId(Long applicationId);
    Pagination<ReceivingHospApplicationEntity> getReceivingHospitalityApplicationList(GiftsApplicationParam param);
}
