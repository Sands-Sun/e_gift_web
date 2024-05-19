package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.ReceivingGiftsActivityDao;
import com.bayer.gifts.process.dao.ReceivingGiftsApplicationDao;
import com.bayer.gifts.process.dao.ReceivingGiftsRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.form.ReceivingGiftsForm;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.bayer.gifts.process.variables.ReceivingGiftsApplyVariable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("receivingGiftsService")
public class ReceivingGiftsServiceImpl implements ReceivingGiftsService {


    @Autowired
    StorageService storageService;
    @Autowired
    GiftsDropDownService giftsDropDownService;

    @Autowired
    GiftsCompanyService giftsCompanyService;

    @Autowired
    GiftsCopyToService giftsCopyToService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ReceivingGiftsRefDao receivingGiftsRefDao;

    @Autowired
    ReceivingGiftsActivityDao receivingGiftsActivityDao;

    @Autowired
    ReceivingGiftsApplicationDao receivingGiftsApplicationDao;

    @Autowired
    GiftsBaseService giftsBaseService;


    @Override
    @MasterTransactional
    public void saveReceivingGifts(ReceivingGiftsForm form) {
        log.info("save receiving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        ReceivingGiftsApplicationEntity application = saveGiftsApplication(currentDate,user,form);
        Long applicationId = application.getApplicationId();
        log.info("applicationId: {}", applicationId);
        saveGiftsActivity(currentDate,application, form);
        Long userId = application.getSfUserIdAppliedFor();
        List<GiftsRelationPersonEntity> giftsPersonList =
                giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,userId,
                        form.getFileId(),Constant.GIFTS_RECEIVING_TYPE);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_RECEIVING_TYPE, form.getCopyToUserEmails(),user);
        List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
        log.info("copy to user emails: {}", copyToUserEmails);
        ReceivingGiftsRefEntity giftsRef = saveGiftsRef(currentDate,applicationId,giftsPersonList,form);
        startProcess(application,user,giftsPersonList,giftsRef,copyToUserEmails,form);
    }


    @Override
    @MasterTransactional
    public void cancelReceivingGifts(ReceivingGiftsForm giftsForm) {
        log.info("cancel receiving gifts...");
        Date currentDate = new Date();
        Long applicationId = giftsForm.getApplicationId();
        ReceivingGiftsApplicationEntity app = receivingGiftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            log.info("empty receiving gifts application...");
            return;
        }
        if(!Constant.GIFTS_DOCUMENTED_TYPE.equals(app.getStatus())){
            log.info("receiving gifts status: {} , can not be cancel", app.getStatus());
            return;
        }
        log.info("cancel receiving gifts application...");
        app.setStatus(Constant.GIFTS_CANCELLED_TYPE);
        receivingGiftsApplicationDao.updateById(app);
        saveGiftsActivity(currentDate,app, giftsForm);
    }

    @Override
    @MasterTransactional
    public void updateReceivingGifts(ReceivingGiftsForm form) {
        log.info("update receiving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        ReceivingGiftsApplicationEntity application = updateGiftsApplication(currentDate,user,form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            log.info("applicationId: {}", applicationId);
            updateGiftsActivity(currentDate,form);
            Long userId = application.getSfUserIdAppliedFor();
            List<GiftsRelationPersonEntity> giftsPersonList =
                    giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,userId,
                            form.getFileId(),Constant.GIFTS_RECEIVING_TYPE);
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_RECEIVING_TYPE, form.getCopyToUserEmails(),user);
            List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            log.info("copy to user emails: {}", copyToUserEmails);
            ReceivingGiftsRefEntity giftsRef = updateGiftsRef(currentDate,applicationId,giftsPersonList,form);
            startProcess(application,user,giftsPersonList,giftsRef,copyToUserEmails,form);
        }
    }

    @Override
    @MasterTransactional
    public void saveUserCase(ReceivingGiftsForm form) {
        log.info("save receiving gifts use case...");
        if(StringUtils.isEmpty(form.getUseCase())){
            return;
        }
        ReceivingGiftsApplicationEntity app = receivingGiftsApplicationDao.selectById(form.getApplicationId());
        if(Objects.isNull(app)){
            log.info("empty receiving gifts application...");
            return;
        }
        app.setUseCase(form.getUseCase());
        receivingGiftsApplicationDao.updateById(app);
    }

    @Override
    public Long copyReceivingGifts(Long application) {
        log.info("copy receiving gifts...");
        return giftsBaseService.copyGiftsRecord(application,Constant.GIFTS_RECEIVING_TYPE);
    }

    @Override
    @MasterTransactional
    public void deleteDraftReceivingGifts(Long applicationId) {
        log.info("delete receiving gifts...");
        ReceivingGiftsApplicationEntity app = receivingGiftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            log.info("empty receiving gifts application...");
            return;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("receiving gifts status: {} , can not be change", app.getStatus());
            return;
        }
        log.info("delete receiving gifts application...");
        receivingGiftsApplicationDao.deleteById(applicationId);
        log.info("delete receiving gifts ref...");
        receivingGiftsRefDao.delete(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery()
                .eq(ReceivingGiftsRefEntity::getApplicationId, applicationId));
        log.info("delete receiving gifts activity...");
        receivingGiftsActivityDao.delete(Wrappers.<ReceivingGiftsActivityEntity>lambdaQuery()
                .eq(GiftsActivityBaseEntity::getApplicationId, applicationId));
        log.info("delete receiving gifts relation person...");
        giftsCompanyService.deleteGiftsRelationPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,
                Constant.GIFTS_RECEIVING_TYPE);
        giftsCopyToService.remove(Wrappers.<GiftsCopyToEntity>lambdaQuery()
                .eq(GiftsCopyToEntity::getApplicationId, applicationId)
                .eq(GiftsCopyToEntity::getType, Constant.GIFTS_RECEIVING_TYPE));
    }


    private ReceivingGiftsApplicationEntity updateGiftsApplication(Date currentDate, UserExtensionEntity user,
                                                                   ReceivingGiftsForm form) {
        log.info("update receiving gifts application...");
        ReceivingGiftsApplicationEntity app = receivingGiftsApplicationDao.selectById(form.getApplicationId());
        if(Objects.isNull(app)){
            log.info("empty receiving gifts application...");
            return null;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("receiving gifts status: {} , can not be change", app.getStatus());
            return null;
        }
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setStatus(form.getActionType());
        String action = Constant.GIFT_SUBMIT_TYPE.equals(form.getActionType()) ?
                Constant.GIFTS_DOCUMENTED_TYPE : form.getActionType();
        log.info("action type: {}", action);
        app.setStatus(action);
        app.setReasonType(form.getReasonType());
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setLastModifiedDate(currentDate);
        app.setEstimatedTotalValue(form.getUnitValue() * form.getVolume());
        receivingGiftsApplicationDao.updateById(app);
        return app;
    }



    private ReceivingGiftsApplicationEntity saveGiftsApplication(Date currentDate, UserExtensionEntity user,
                                                              ReceivingGiftsForm form) {
        log.info("save receiving gifts application...");
        ReceivingGiftsApplicationEntity app = new ReceivingGiftsApplicationEntity();
        String reference = giftsDropDownService.getReference();
        BeanUtils.copyProperties(user,app);
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setSfUserIdCreator(user.getSfUserId());
        app.setSupervisorId(user.getSupervisorId());
        app.setEmployeeLe(user.getCompanyCode());
        app.setDepartment(user.getOrgTxt());
        app.setReasonType(form.getReasonType());
        //默认为合规
        String isHandedOver = StringUtils.isEmpty(form.getIsHandedOver()) ? "Y" : form.getIsHandedOver();
        app.setIsHandedOver(Constant.EXIST_MARK.equals(isHandedOver) ? "Y" : "N");
        app.setIsExcluded(form.getIsExcluded());
        app.setIsInvolved(form.getIsInvolved());

        String action = Constant.GIFT_SUBMIT_TYPE.equals(form.getActionType()) ?
                Constant.GIFTS_DOCUMENTED_TYPE : form.getActionType();
        log.info("action type: {}", action);
        app.setStatus(action);
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setReference(reference);
        app.setEstimatedTotalValue(form.getUnitValue() * form.getVolume());
//        if(Objects.nonNull(form.getEstimatedTotalValue())){
//            app.setEstimatedTotalValue(form.getEstimatedTotalValue());
//        }else {
//            app.setEstimatedTotalValue(form.getUnitValue() * form.getVolume());
//        }
        app.setCreatedDate(currentDate);
        app.setLastModifiedDate(currentDate);
        app.setMarkDeleted(Constant.NO_EXIST_MARK);
        app.setNewVersion(Constant.EXIST_MARK);
        receivingGiftsApplicationDao.insert(app);
        return app;
    }


    private void updateGiftsActivity(Date currentDate, ReceivingGiftsForm form) {
        log.info("update receiving gifts activity...");
        ReceivingGiftsActivityEntity activity = receivingGiftsActivityDao.
                selectOne(Wrappers.<ReceivingGiftsActivityEntity>lambdaQuery().
                eq(ReceivingGiftsActivityEntity::getApplicationId,form.getApplicationId()));
        if(Objects.isNull(activity)){
            log.info("empty receiving gifts activity...");
            return;
        }
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setLastModifiedDate(currentDate);
        receivingGiftsActivityDao.updateById(activity);
    }

    private void saveGiftsActivity(Date currentDate,ReceivingGiftsApplicationEntity application,
                                   ReceivingGiftsForm form) {
        log.info("save receiving gifts activity...");
        ReceivingGiftsActivityEntity activity = new ReceivingGiftsActivityEntity();
        activity.setApplicationId(application.getApplicationId());
        activity.setSfUserIdSubmitter(application.getSfUserIdCreator());
        String actionType = Constant.GIFTS_DRAFT_TYPE.equals(form.getActionType()) ? Constant.GIFTS_SAVE_TYPE : form.getActionType();
        activity.setAction(actionType);
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        receivingGiftsActivityDao.insert(activity);
    }


    private ReceivingGiftsRefEntity saveGiftsRef(Date currentDate, Long applicationId,
                              List<GiftsRelationPersonEntity> giftsPersonList,
                              ReceivingGiftsForm form) {
        log.info("save receiving gifts reference...");
        String givingPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givingCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName)
                .collect(Collectors.joining(","));
        log.info("give persons: {}", givingPersons);
        log.info("give company: {}", givingCompany);
        String isHandeOver = form.getIsHandedOver();
//        String isSco;
//        if(Constant.EXIST_MARK.equals(isHandeOver)) {
//            isSco = Constant.YES_MARK;
//        }else if(Constant.NO_EXIST_MARK.equals(isHandeOver)){
//            isSco = Constant.NO_MARK;
//        }else {
//            isSco = Constant.GIFTS_NOT_APPLICABLE;
//        }
        ReceivingGiftsRefEntity giftsRef = new ReceivingGiftsRefEntity();
        BeanUtils.copyProperties(form,giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setGivingCompany(givingCompany);
        giftsRef.setGivingPerson(givingPersons);
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setIsSco(Constant.YES_MARK);
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
        receivingGiftsRefDao.insert(giftsRef);
        return giftsRef;
    }


    private ReceivingGiftsRefEntity updateGiftsRef(Date currentDate, Long applicationId,
                                List<GiftsRelationPersonEntity> giftsPersonList, ReceivingGiftsForm form) {
        log.info("update receiving gifts reference...");
        ReceivingGiftsRefEntity giftsRef = receivingGiftsRefDao.selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery()
                .eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return null;
        }
        String givingPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givingCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName)
                .collect(Collectors.joining(","));
        log.info("give persons: {}", givingPersons);
        log.info("give company: {}", givingCompany);

//        String isSco = Constant.EXIST_MARK.equals(form.getIsHandedOver()) ? "Yes" : "No";
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setGiftDesc(form.getGiftDesc());
        giftsRef.setGiftDescType(form.getGiftDescType());
        giftsRef.setUnitValue(form.getUnitValue());
        giftsRef.setVolume(form.getVolume());
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setIsSco(Constant.YES_MARK);
        giftsRef.setGivingPerson(givingPersons);
        giftsRef.setGivingCompany(givingCompany);
        giftsRef.setLastModifiedDate(currentDate);
        receivingGiftsRefDao.updateById(giftsRef);
        return giftsRef;
    }


    @Override
    public ReceivingGiftsApplicationEntity getReceivingGiftsByApplicationId(Long applicationId) {
        log.info("get receiving gifts: {}",applicationId);
        ReceivingGiftsApplicationEntity application = receivingGiftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(application)){
            return null;
        }
        application.setDisableUseCase(StringUtils.isNotEmpty(application.getUseCase()));
        UserExtensionEntity user = userInfoService.getById(application.getSfUserIdAppliedFor());
        if(Objects.nonNull(user)){
            application.setSfUserAppliedName(user.getFirstName() + " " + user.getLastName());
            application.setSfUserAppliedEmail(user.getEmail());
            application.setSfUserAppliedCwid(user.getCwid());
        }
        ReceivingGiftsRefEntity references = receivingGiftsRefDao.
                selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery().
                        eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,Constant.GIFTS_RECEIVING_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,
                Constant.GIFTS_RECEIVING_TYPE);
        log.info("Company size: {}", companyList.size());
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<ReceivingGiftsActivityEntity> giftsActivities =
                receivingGiftsApplicationDao.queryReceivingGiftsActivityList(activityParam);
        log.info("giftsActivities size: {}", giftsActivities.size());
        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,Constant.GIFTS_RECEIVING_TYPE,"CompanyPerson");
        if(Objects.nonNull(fileAttach)){
            log.info("gifts file attachment: {}", fileAttach.getFileName());
            application.setFileAttach(fileAttach);
        }
        application.setGiftsRef(references);
        application.setCopyToUsers(copyToUsers);
        application.setGiftsActivities(giftsActivities);
        application.setCompanyList(companyList);
        return application;
    }

    @Override
    public Pagination<ReceivingGiftsApplicationEntity> getReceivingGiftsApplicationList(GiftsApplicationParam param) {
        log.info("get receiving gifts page...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        IPage<ReceivingGiftsApplicationEntity> page = receivingGiftsApplicationDao.queryReceivingGiftsApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }



    private void startProcess(ReceivingGiftsApplicationEntity application, UserExtensionEntity user,
                              List<GiftsRelationPersonEntity> giftsPersonList,
                              ReceivingGiftsRefEntity giftsRef, List<String> copyToUserEmails, ReceivingGiftsForm form) {
        if(Constant.GIFT_SUBMIT_TYPE.equals(form.getActionType())){
            log.info("start receiving gifts process...");
            Map<String, Object> variables = Maps.newHashMap();
            ReceivingGiftsApplyVariable applyVar = copyInforToApplyVar(user,application,giftsPersonList,giftsRef,copyToUserEmails);
            variables.put(Constant.GIFTS_APPLY_RECEIVING_GIFTS_VARIABLE, applyVar);
            ReceivingGiftsActivityEntity lastOneActivity = receivingGiftsApplicationDao.queryReceivingGiftsActivityLastOne(application.getApplicationId());
            giftsBaseService.updateAndProcessBusiness(application,lastOneActivity,variables,application.getRemark(),
                    Constant.GIFTS_RECEIVING_TYPE,Constant.GIFTS_DOCUMENTED_TYPE,Constant.GIFTS_DOCUMENTED_TYPE,false);
        }
    }

    private ReceivingGiftsApplyVariable copyInforToApplyVar(UserExtensionEntity user,
                                                            ReceivingGiftsApplicationEntity application,
                                                            List<GiftsRelationPersonEntity> giftsPersonList,
                                                            ReceivingGiftsRefEntity giftsRef,
                                                            List<String> copyToUserEmails) {
        ReceivingGiftsApplyVariable variable = new ReceivingGiftsApplyVariable();
        BeanUtils.copyProperties(user,variable);
        BeanUtils.copyProperties(application,variable);
        BeanUtils.copyProperties(giftsRef,variable);
        variable.setActionType(application.getStatus());
        variable.setApplyForId(application.getSfUserIdAppliedFor());
        //ToDo change apply email when apply and create are not same person
        variable.setApplyEmail(user.getEmail());
        variable.setEstimatedTotalValue(giftsRef.getUnitValue() * giftsRef.getVolume());
        variable.setApplyDate(DateUtils.dateToStr(
                application.getCreatedDate(), DateUtils.DATE_PATTERN));
        variable.setCreatorName(user.getFirstName() + " " + user.getLastName());
        variable.setGivingDate(DateUtils.dateToStr(
                giftsRef.getGivingDate(), DateUtils.DATE_PATTERN));
        if(!Objects.equals(application.getSfUserIdAppliedFor(), application.getSfUserIdCreator())){
            log.info("apply for and creator are not same person...");
            UserExtensionEntity applyForUser = userInfoService.getById(application.getSfUserIdAppliedFor());
            variable.setApplyForName(applyForUser.getFirstName() + " " + applyForUser.getLastName());
        }else {
            variable.setApplyForName(user.getFirstName() + " " + user.getLastName());
        }
//        variable.setgiv(giftsRef.getGivingPerson());
        variable.setGiftsPersonList(giftsPersonList);
        variable.setReferenceNo(application.getReference());
        variable.setApplicationId(application.getApplicationId());
        if(CollectionUtils.isNotEmpty(copyToUserEmails)){
            variable.setCopyToUserEmails(copyToUserEmails);
        }
        UserExtensionEntity supervisor = user.getSupervisor();
        if(Objects.nonNull(supervisor)){
            variable.setSupervisorId(supervisor.getSfUserId());
            variable.setSupervisorName(supervisor.getLastName() + " " +
                    supervisor.getFirstName());
            variable.setSupervisorMail(supervisor.getEmail());
        }
        return variable;
    }
}
