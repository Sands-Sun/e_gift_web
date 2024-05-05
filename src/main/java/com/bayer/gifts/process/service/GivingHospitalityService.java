package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.HospitalityApplicationEntity;
import com.bayer.gifts.process.form.GivingHospitalityFrom;
import com.bayer.gifts.process.param.GiftsApplicationParam;

public interface GivingHospitalityService {

    void updateGivingHospitality(GivingHospitalityFrom form);

    void saveGivingHospitality(GivingHospitalityFrom form);

    void cancelGivingHospitality(GivingHospitalityFrom form);

    void deleteDraftGivingHospitality(Long applicationId);

    HospitalityApplicationEntity getGivingHospitalityByApplicationId(Long applicationId);

    Pagination<HospitalityApplicationEntity> getGivingHospitalityApplicationList(GiftsApplicationParam param);
}
