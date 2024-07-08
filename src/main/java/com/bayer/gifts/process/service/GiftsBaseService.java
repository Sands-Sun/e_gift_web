package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsActivityBaseEntity;
import com.bayer.gifts.process.entity.GiftsApplicationBaseEntity;
import com.bayer.gifts.process.entity.GiftsApplicationProcessEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.form.FormBase;
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import org.activiti.engine.delegate.event.ActivitiEvent;

import java.util.Map;

public interface GiftsBaseService {
    String getProcessInstanceKey(UserExtensionEntity user, String processTypePrefix);
    void fillInUserInfo(GiftsApplicationBaseEntity app);
    void fillInApplyForUser(UserExtensionEntity user, GiftsApplicationBaseEntity app);
    void fillInCountryHead(GiftsApplicationProcessEntity app);
    void fillInDepartmentHead(GiftsApplicationProcessEntity app);
    void fillInDepartmentHead(GiftsApplicationProcessEntity app, FormBase form,String division);
    Long copyGiftsRecord(Long applicationId,String type);
    void copyToGiftsProcess(GiftsApplyBaseVariable variable, String type);
    void setSignatureAndRemark(GiftsApplyBaseVariable variable, String type);

    void cancelGiftsProcess(FormBase form, String type);

    void documentGiftsProcess(ActivitiEvent event);

    GiftsActivityBaseEntity getGiftsActivityBaseLastOne(Long applicationId, String type);

    boolean updateAndProcessBusiness(GiftsApplicationBaseEntity app,
                                     GiftsActivityBaseEntity activity,
                                     Map<String, Object> runtimeVar, String remark, String type,
                                     String status, String actionType, boolean needUpt);
}
