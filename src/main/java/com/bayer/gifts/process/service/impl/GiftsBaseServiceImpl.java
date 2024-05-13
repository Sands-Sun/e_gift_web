package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.*;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.GiftsBaseNoticeMailVo;
import com.bayer.gifts.process.mail.vo.GivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.mail.vo.GivingHospProcessNoticeMailVo;
import com.bayer.gifts.process.mail.vo.ReceivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.service.GiftsBaseService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.variables.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service("giftsBaseService")
public class GiftsBaseServiceImpl implements GiftsBaseService {

    @Autowired
    GivingGiftsApplicationDao givingGiftsApplicationDao;
    @Autowired
    GivingHospitalityApplicationDao hospitalityApplicationDao;
    @Autowired
    ReceivingGiftsApplicationDao receivingGiftsApplicationDao;
    @Autowired
    GivingGiftsActivityDao givingGiftsActivityDao;
    @Autowired
    ReceivingGiftsActivityDao receivingGiftsActivityDao;
    @Autowired
    GivingHospitalityActivityDao givingHospitalityActivityDao;
    @Autowired
    BatchCompleteMailService completeMailService;
    @Autowired
    RuntimeService runtimeService;

    @Override
    public void documentGiftsProcess(ActivitiEvent event) {
        log.info("document gifts...");
        String processId = event.getProcessInstanceId();
        String definitionId =  event.getProcessDefinitionId();
        Map<String, Object> runtimeVar = runtimeService.getVariables(processId);
        String type;
        if(definitionId.startsWith(Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX)) {
            type = Constant.GIFTS_GIVING_TYPE;
        }else if(definitionId.startsWith(Constant.GIVING_HOSP_PROCESS_TYPE_PREFIX)){
            type = Constant.HOSPITALITY_TYPE;
        }else {
            type = Constant.GIFTS_GIVING_TYPE;
        }
        GiftsApplicationProcessEntity app = getGiftsApplicationByProcId(Long.valueOf(processId),type);
        if(Objects.isNull(app)){
            log.info("empty gifts application...");
            return;
        }
        Long applicationId = app.getApplicationId();
        GiftsActivityBaseEntity activity = getGiftsActivityBaseLastOne(applicationId,type);
        if(Constant.GIFTS_APPROVE_TYPE.equals(activity.getAction())){
            updateAndProcessBusiness(app,activity,runtimeVar,Constant.GIFTS_DOCUMENTED_TYPE,
                    type, Constant.GIFTS_APPROVE_TYPE,Constant.GIFTS_DOCUMENTED_TYPE,true);
        }
    }

    @Override
    public void cancelGiftsProcess(GiftsFormBase form, String type) {
        log.info("cancel gifts...");
        Long applicationId = form.getApplicationId();
        GiftsApplicationProcessEntity app = getGiftsApplicationById(applicationId,type);
        if(Objects.isNull(app)){
            log.info("empty gifts application...");
            return;
        }
        String  processInsId = String.valueOf(app.getSfProcessInsId());
        log.info("gifts processInsId: {}", processInsId);
        Map<String, Object> runtimeVar  = runtimeService.getVariables(processInsId);
        GiftsActivityBaseEntity activity = getGiftsActivityBaseLastOne(applicationId,type);
        if(updateAndProcessBusiness(app,activity,runtimeVar,type,
                Constant.GIFTS_CANCELLED_TYPE,Constant.GIFTS_CANCELLED_TYPE, Constant.GIFTS_CANCELLED_TYPE,true)){
            runtimeService.deleteProcessInstance(processInsId, form.getRemark());
        }
    }


    @Override
    public void setSignatureAndRemark(GiftsApplyBaseVariable variable, String type) {
        log.info("set signature and remark type >>> {}",type);
        List<String> signatureList = Lists.newArrayList();
        List<String> remarkList = Lists.newArrayList();
        List<? extends GiftsActivityBaseEntity> activities = getGiftsActivityBaseByType(variable.getApplicationId(), type);
        for(GiftsActivityBaseEntity a : activities){
            String signUserName = a.getUserFirstName() + StringUtils.SPACE + a.getUserLastName();
            String createDate = DateUtils.dateToStr(a.getCreatedDate(),DateUtils.DATE_TIME_PATTERN);
            String signature = signUserName + StringUtils.SPACE +  a.getAction() + StringUtils.SPACE + createDate;
            String remarkTrank = signUserName + StringUtils.SPACE + "wrote at" + StringUtils.SPACE + createDate;
            log.info("signature ---> {}", signature);
            String remark = String.format("<b>%s</b></br>",remarkTrank) + a.getRemark();
            log.info("remark ---> {}", remark);
            signatureList.add(signature);
            remarkList.add(remark);
        }
        variable.setSignatureList(signatureList);
        variable.setRemarkList(remarkList);
    }
    @Override
    public boolean updateAndProcessBusiness(GiftsApplicationBaseEntity app,GiftsActivityBaseEntity activity,
                                             Map<String, Object> runtimeVar, String remark, String type,
                                             String status,String actionType, boolean needUpt) {
        Date currentDate = new Date();
        if(Objects.nonNull(activity) && Constant.GIFTS_REJECTED_TYPE.equals(activity.getAction())){
            log.info("already reject no need cancel...");
            return false;
        }
        GiftsBaseNoticeMailVo noticeMailVo = null;
        GiftsApplyBaseVariable variable;
        if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            variable = (ReceivingGiftsApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_RECEIVING_GIFTS_VARIABLE);
            //TODO init receiving notice mail vo
            noticeMailVo = new ReceivingGiftsProcessNoticeMailVo(variable);
        }else if(Constant.HOSPITALITY_TYPE.equals(type)) {
            variable = (GivingHospApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE);
            //ToDo init hospitality notice mail vo
            noticeMailVo = new GivingHospProcessNoticeMailVo();
        }else {
            variable = (GivingGiftsApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE);
            new GivingGiftsProcessNoticeMailVo(variable);
        }
        variable.setActionType(actionType);
        if(needUpt) {
            app.setStatus(status);
            updateApplicationById(type,app);
            saveGiftsActivity(currentDate,type,status,remark,app);
        }
        setSignatureAndRemark(variable,type);
        processSendMail(noticeMailVo);
        return true;
    }



    private void processSendMail(GiftsBaseNoticeMailVo noticeMailVo) {
        BatchCompleteMail completeMail = completeMailService.saveCompleteMail(noticeMailVo);
        if(Objects.nonNull(completeMail)){
            try {
                MailUtils.sendMail(completeMail);
            } catch (MessagingException | IOException e) {
                log.error("send mail error",e);
            }
        }
    }


    private GiftsApplicationProcessEntity getGiftsApplicationByProcId(Long processId, String type) {
        GiftsApplicationProcessEntity app;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            app = hospitalityApplicationDao.selectOne(
                    Wrappers.<HospitalityApplicationEntity>lambdaQuery()
                            .eq(HospitalityApplicationEntity::getSfProcessInsId, processId));
        } else {
            app = givingGiftsApplicationDao.selectOne(
                    Wrappers.<GivingGiftsApplicationEntity>lambdaQuery()
                            .eq(GivingGiftsApplicationEntity::getSfProcessInsId, processId));
        }
        return app;
    }
    private GiftsApplicationProcessEntity getGiftsApplicationById(Long applicationId,String type) {
        GiftsApplicationProcessEntity app;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            app = hospitalityApplicationDao.selectById(applicationId);
        } else {
            app = givingGiftsApplicationDao.selectById(applicationId);
        }
        return app;
    }

    private GiftsActivityBaseEntity getGiftsActivityBaseLastOne(Long applicationId, String type) {
        GiftsActivityBaseEntity activity = null;
        //            case Constant.GIFTS_RECEIVING_TYPE:
        //                activity = receivingGiftsApplicationDao.queryReceivingGiftsActivityLastOne(applicationId);
        //                break;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            activity = hospitalityApplicationDao.queryGivingHospitalityActivityLastOne(applicationId);
        } else {
            activity = givingGiftsApplicationDao.queryGivingGiftsActivityLastOne(applicationId);
        }
        return activity;
    }

    private List<? extends GiftsActivityBaseEntity> getGiftsActivityBaseByType(Long applicationId,String type) {
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<? extends GiftsActivityBaseEntity> activities;
        switch (type){
            case Constant.GIFTS_RECEIVING_TYPE:
                activities = receivingGiftsApplicationDao.queryReceivingGiftsActivityList(activityParam);
                break;
            case Constant.HOSPITALITY_TYPE:
                activities = hospitalityApplicationDao.queryGivingHospitalityActivityList(activityParam);
                break;
            default:
                activities = givingGiftsApplicationDao.queryGivingGiftsActivityList(activityParam);
        }
        return activities;
    }

    private void saveGiftsActivity(Date currentDate, String type,String status,String remark, GiftsApplicationBaseEntity app) {
        log.info("save gifts activity...");
        if(Constant.GIFTS_GIVING_TYPE.equals(type)){
            GivingGiftsActivityEntity activity = fillInActivityEntity(currentDate,status,remark,app,GivingGiftsActivityEntity.class);
            givingGiftsActivityDao.insert(activity);
        }else if(Constant.HOSPITALITY_TYPE.equals(type)) {
            HospitalityActivityEntity activity = fillInActivityEntity(currentDate,status,remark,app, HospitalityActivityEntity.class);
            givingHospitalityActivityDao.insert(activity);
        }else if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            ReceivingGiftsActivityEntity activity = fillInActivityEntity(currentDate,status,remark,app,ReceivingGiftsActivityEntity.class);
            receivingGiftsActivityDao.insert(activity);
        }
    }

    private void updateApplicationById(String type,GiftsApplicationBaseEntity app) {
        log.info("update application type: {}, id: {}", type, app.getApplicationId());
        switch (type){
            case Constant.GIFTS_RECEIVING_TYPE:
                receivingGiftsApplicationDao.updateById((ReceivingGiftsApplicationEntity)app);
                break;
            case Constant.HOSPITALITY_TYPE:
                hospitalityApplicationDao.updateById((HospitalityApplicationEntity) app);
                break;
            default:
                givingGiftsApplicationDao.updateById((GivingGiftsApplicationEntity) app);
        }
    }


    private <T extends GiftsActivityBaseEntity> T fillInActivityEntity(Date currentDate,String status,String remark,
                                                                          GiftsApplicationBaseEntity app, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            t.setApplicationId(app.getApplicationId());
            t.setSfUserIdSubmitter(app.getSfUserIdCreator());
            t.setAction(status);
            t.setRemark(remark);
            t.setCreatedDate(currentDate);
            t.setLastModifiedDate(currentDate);
            return t;
        } catch (InstantiationException e) {
            log.error("InstantiationException", e);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
        }
        return null;
    }
}
