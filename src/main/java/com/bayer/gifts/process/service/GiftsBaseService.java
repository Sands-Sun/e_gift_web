package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsActivityBaseEntity;
import com.bayer.gifts.process.entity.GiftsApplicationBaseEntity;
import com.bayer.gifts.process.form.FormBase;
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import org.activiti.engine.delegate.event.ActivitiEvent;

import java.util.Map;

public interface GiftsBaseService {

    Long copyGiftsRecord(Long applicationId,String type);
    void copyToGiftsProcess(GiftsApplyBaseVariable variable, String type);
    void setSignatureAndRemark(GiftsApplyBaseVariable variable, String type);

    void cancelGiftsProcess(FormBase form, String type);

    void documentGiftsProcess(ActivitiEvent event);

    boolean updateAndProcessBusiness(GiftsApplicationBaseEntity app,
                                     GiftsActivityBaseEntity activity,
                                     Map<String, Object> runtimeVar, String remark, String type,
                                     String status, String actionType, boolean needUpt);
}
