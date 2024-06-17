package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.GivingHospApplicationEntity;
import com.bayer.gifts.process.form.GivingHospitalityFrom;
import com.bayer.gifts.process.param.GiftsApplicationParam;

public interface GivingHospitalityService {

    Long copyGivingHospitality(Long application);
    void updateGivingHospitality(GivingHospitalityFrom form);

    void saveGivingHospitality(GivingHospitalityFrom form);

    void cancelGivingHospitality(GivingHospitalityFrom form);

    void deleteDraftGivingHospitality(Long applicationId);

    GivingHospApplicationEntity getGivingHospitalityByApplicationId(Long applicationId);
    GivingHospApplicationEntity getGivingHospitalityHistoryByApplicationId(Long applicationId);

    Pagination<GivingHospApplicationEntity> getGivingHospitalityApplicationList(GiftsApplicationParam param);
}
