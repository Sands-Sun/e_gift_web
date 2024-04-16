package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.GiftsCopyToDao;
import com.bayer.gifts.process.dao.GivingGiftsActivityDao;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.dao.GiftsRelationPersonDao;
import com.bayer.gifts.process.dao.GivingGiftsRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("givingGiftsService")
public class GivingGiftsServiceImpl implements GivingGiftsService {

    @Autowired
    ShiroService shiroService;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    GiftsDropDownService giftsDropDownService;

    @Autowired
    GiftsCompanyService giftsCompanyService;

    @Autowired
    GiftsCopyToService giftsCopyToService;

    @Autowired
    GivingGiftsApplicationDao giftsApplicationDao;

    @Autowired
    GivingGiftsActivityDao givingGiftsActivityDao;

    @Autowired
    GivingGiftsRefDao givingGiftsRefDao;

    @Autowired
    RuntimeService runtimeService;


    @Override
    @MasterTransactional
    public void updateDraftGivingGifts(GivingGiftsForm form) {
        log.info("update giving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        //mock begin >>>>>
//        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
        //mock end >>>>>
        GivingGiftsApplicationEntity application = updateGiftsApplication(currentDate,user, form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            Long userId = application.getSfUserIdAppliedFor();
            log.info("applicationId: {}", applicationId);
            GivingGiftsActivityEntity activity = updateGiftsActivity(currentDate, form);
            List<GiftsRelationPersonEntity> giftsPersonList =
                    giftsCompanyService.saveOrUpdateGiftsPerson(currentDate,applicationId,
                            userId,StringUtils.EMPTY,form.getGivenCompany(),form.getGivenPersons(), form.getUnitValue());
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,"Giving", form.getCopyToUserEmails(),user);
            List<Long> copyToUserIds = copyToList.stream().map(GiftsCopyToEntity::getSfUserIdCopyTo).collect(Collectors.toList());
            log.info("copy to user ids: {}", copyToUserIds);
            GivingGiftsRefEntity giftsRef = updateGiftsRef(currentDate,applicationId,giftsPersonList, form);
            startProcess(application,user,giftsRef,activity,copyToUserIds, form);
        }
    }

    @Override
    @MasterTransactional
    public void saveGivingGifts(GivingGiftsForm form) {
        log.info("save giving gifts...");
        Date currentDate = new Date();
//        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        //mock begin >>>>>
        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
        //mock end >>>>>
        GivingGiftsApplicationEntity application = saveGiftsApplication(currentDate,user, form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info("applicationId: {}", applicationId);
        GivingGiftsActivityEntity activity = saveGiftsActivity(currentDate,application, form);
        List<GiftsRelationPersonEntity> giftsPersonList =
                giftsCompanyService.saveOrUpdateGiftsPerson(currentDate,applicationId,
                        userId,StringUtils.EMPTY,form.getGivenCompany(),form.getGivenPersons(), form.getUnitValue());
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,"Giving", form.getCopyToUserEmails(),user);
        List<Long> copyToUserIds = copyToList.stream().map(GiftsCopyToEntity::getSfUserIdCopyTo).collect(Collectors.toList());
        log.info("copy to user ids: {}", copyToUserIds);
        GivingGiftsRefEntity giftsRef = saveGiftsRef(currentDate,applicationId,giftsPersonList, form);
        startProcess(application,user,giftsRef,activity,copyToUserIds, form);
    }



    private void startProcess(GivingGiftsApplicationEntity application, UserExtensionEntity user,
                              GivingGiftsRefEntity giftsRef,GivingGiftsActivityEntity activity,
                              List<Long> copyToUserIds, GivingGiftsForm GivingGiftsForm) {
        if(Constant.GIFT_SUBMIT_TYPE.equals(GivingGiftsForm.getActionType())){
            Map<String, Object> variables = Maps.newHashMap();
            GivingGiftsApplyVariable applyVar = copyInforToApplyVar(user,application,giftsRef,copyToUserIds);
            variables.put("applyGivingGiftsVar", applyVar);
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceByKey("givingGifts_" + user.getCompanyCode(), variables);
            Long processId = Long.valueOf(processInstance.getId());
            log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());

            giftsApplicationDao.update(null, Wrappers.<GivingGiftsApplicationEntity>lambdaUpdate()
                    .set(GivingGiftsApplicationEntity::getSfProcessInsId, processId)
                    .eq(GivingGiftsApplicationEntity::getApplicationId,application.getApplicationId()));
            givingGiftsActivityDao.update(null, Wrappers.<GivingGiftsActivityEntity>lambdaUpdate()
                    .set(GivingGiftsActivityEntity::getSfProcessInsId, processId)
                    .eq(GivingGiftsActivityEntity::getApplicationId,application.getApplicationId()));
        }
    }


    @Override
    public GivingGiftsApplicationEntity getGivingGiftsByApplicationId(Long applicationId) {
        log.info("get giving gifts: {}",applicationId);
        GivingGiftsApplicationEntity application = giftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(application)){
            return null;
        }
        GivingGiftsRefEntity references = givingGiftsRefDao.selectOne(Wrappers.<GivingGiftsRefEntity>lambdaQuery().
                eq(GivingGiftsRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.list(Wrappers.<GiftsCopyToEntity>lambdaQuery().
                eq(GiftsCopyToEntity::getApplicationId,applicationId)
                .eq(GiftsCopyToEntity::getType,"Giving"));
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsRelationPersonEntity> giftsPersons = giftsCompanyService.getGiftsRelationPersonByApplicationId(applicationId);
        log.info("giftsPerson size: {}", giftsPersons.size());
        List<GivingGiftsActivityEntity> giftsActivities =
                giftsApplicationDao.queryGivingGiftsActivityList(applicationId,null);
        log.info("giftsActivities size: {}", giftsActivities.size());
        application.setGiftsRef(references);
        application.setCopyToUsers(copyToUsers);
        application.setGiftsPersons(giftsPersons);
        application.setGiftsActivities(giftsActivities);
        return application;
    }



    @Override
    public Pagination<GivingGiftsApplicationEntity> getGivingGiftsApplicationList(GiftsApplicationParam param) {
        log.info("get giving gifts page...");
//        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
//        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        IPage<GivingGiftsApplicationEntity> page = giftsApplicationDao.queryGivingGiftsApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }


    private GivingGiftsApplyVariable copyInforToApplyVar(UserExtensionEntity user,
                                                         GivingGiftsApplicationEntity application,
                                                         GivingGiftsRefEntity giftsRef,
                                                         List<Long> copyToUserIds) {
        GivingGiftsApplyVariable variable = new GivingGiftsApplyVariable();
        BeanUtils.copyProperties(user,variable);
        BeanUtils.copyProperties(application,variable);
        BeanUtils.copyProperties(giftsRef,variable);
        variable.setActionType(Constant.GIFT_SUBMIT_TYPE);
        variable.setApplyForId(application.getSfUserIdAppliedFor());
        //ToDo change apply email when apply and create are not same person
        variable.setApplyEmail(user.getEmail());
        variable.setTotalValue(giftsRef.getUnitValue() * giftsRef.getVolume());
        variable.setApplyDate(DateUtils.dateToStr(
                application.getCreatedDate(), DateUtils.DATE_PATTERN));
        variable.setCreatorName(user.getFirstName() + " " + user.getLastName());
        variable.setGivenDate(DateUtils.dateToStr(
                giftsRef.getGivenDate(), DateUtils.DATE_PATTERN));
        if(!Objects.equals(application.getSfUserIdAppliedFor(), application.getSfUserIdCreator())){
            log.info("apply for and creator are not same person...");
            UserExtensionEntity applyForUser = userInfoService.getById(application.getSfUserIdAppliedFor());
            variable.setApplyForName(applyForUser.getFirstName() + " " + applyForUser.getLastName());
        }else {
            variable.setApplyForName(user.getFirstName() + " " + user.getLastName());
        }
        variable.setGivenPersons(giftsRef.getGivenPerson());
        variable.setReferenceNo(application.getReference());
        variable.setApplicationId(application.getApplicationId());
        if(CollectionUtils.isNotEmpty(copyToUserIds)){
            variable.setCopyToUserIds(copyToUserIds);
        }
        UserExtensionEntity supervisor = user.getSupervisor();
        if(Objects.nonNull(supervisor)){
            variable.setSupervisorId(supervisor.getSfUserId());
            variable.setSupervisorName(supervisor.getLastName() + " " +
                    supervisor.getFirstName());
            variable.setSupervisorMail(supervisor.getEmail());
        }
        variable.fillInExtraVar(user.getCompanyCode(),user.getBizGroup());

        return variable;
    }





    private GivingGiftsRefEntity updateGiftsRef(Date currentDate, Long applicationId,
                                                List<GiftsRelationPersonEntity> giftsPersonList, GivingGiftsForm form) {
        log.info("update giving gifts reference...");
        GivingGiftsRefEntity giftsRef = givingGiftsRefDao.selectOne(Wrappers.<GivingGiftsRefEntity>lambdaQuery()
                .eq(GivingGiftsRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return null;
        }
        String givenPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givenCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName).
                findFirst().orElse(StringUtils.EMPTY);
        log.info("give persons: {}", givenPersons);
        log.info("give company: {}", givenCompany);
//        giftsRef.setGiftDesc(form.getGiftsDescription());
//        giftsRef.setGiftDescType(form.getGiftsDescriptionType());
        giftsRef.setGivenPerson(givenPersons);
        giftsRef.setGivenCompany(givenCompany);
        giftsRef.setGivenDate(form.getDate());
        giftsRef.setLastModifiedDate(currentDate);
//            giftsRef.setGivingDate();
//            giftsRef.setAttachmentFile();
//            giftsRef.setAttachmentFileName();
//            giftsRef.setCategoryId();
//            giftsRef.setCategoryName();
//        private String attachmentFile;
//        private String attachmentFileName;
        givingGiftsRefDao.updateById(giftsRef);
        return giftsRef;
    }

    private GivingGiftsRefEntity saveGiftsRef(Date currentDate, Long applicationId,
                                              List<GiftsRelationPersonEntity> giftsPersonList, GivingGiftsForm form) {
        log.info("save giving gifts reference...");
        String givenPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givenCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName).
                findFirst().orElse(StringUtils.EMPTY);
        log.info("give persons: {}", givenPersons);
        log.info("give company: {}", givenCompany);
        GivingGiftsRefEntity giftsRef = new GivingGiftsRefEntity();
        giftsRef.setApplicationId(applicationId);
        BeanUtils.copyProperties(form, giftsRef);
//        giftsRef.setUnitValue(form.getUnitValue());
//        giftsRef.setVolume(form.getVolume());
//        giftsRef.setGoSocFlag(form.getIsGoSoc());
//        giftsRef.setBayerCustomerFlag(form.getIsBayerCustomer());
//        giftsRef.setGivenCompany();
//        giftsRef.setGiftDesc(form.getGiftsDescription());
//        giftsRef.setGiftDescType(form.getGiftsDescriptionType());
        giftsRef.setGivenPerson(givenPersons);
        giftsRef.setGivenCompany(givenCompany);
        giftsRef.setGivenDate(form.getDate());
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
//            giftsRef.setGivingDate();
//            giftsRef.setAttachmentFile();
//            giftsRef.setAttachmentFileName();
//            giftsRef.setCategoryId();
//            giftsRef.setCategoryName();
//        private String attachmentFile;
//        private String attachmentFileName;
        givingGiftsRefDao.insert(giftsRef);
        return giftsRef;
    }


    private GivingGiftsActivityEntity updateGiftsActivity(Date currentDate, GivingGiftsForm form) {
        log.info("update giving gifts activity...");
        GivingGiftsActivityEntity activity = givingGiftsActivityDao.selectOne(Wrappers.<GivingGiftsActivityEntity>lambdaQuery().
                eq(GivingGiftsActivityEntity::getApplicationId,form.getApplicationId()));
        if(Objects.isNull(activity)){
            log.info("empty giving gifts activity...");
            return null;
        }
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setLastModifiedDate(currentDate);
        givingGiftsActivityDao.updateById(activity);
        return activity;
    }

    private GivingGiftsActivityEntity saveGiftsActivity(Date currentDate,GivingGiftsApplicationEntity application,
                                                        GivingGiftsForm form) {
        log.info("save giving gifts activity...");
        GivingGiftsActivityEntity activity = new GivingGiftsActivityEntity();
        activity.setApplicationId(application.getApplicationId());
        activity.setSfUserIdSubmitter(application.getSfUserIdCreator());
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        givingGiftsActivityDao.insert(activity);
        return activity;
    }



    private GivingGiftsApplicationEntity updateGiftsApplication(Date currentDate, UserExtensionEntity user, GivingGiftsForm form) {
        log.info("update giving gifts application...");
        GivingGiftsApplicationEntity app = giftsApplicationDao.selectById(form.getApplicationId());
        if(Objects.isNull(app)){
            log.info("empty giving gifts application...");
            return null;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("giving gifts status: {} , can not be change", app.getStatus());
            return null;
        }
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setStatus(form.getActionType());
        app.setReasonType(form.getReasonType());
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setLastModifiedDate(currentDate);
        app.setTotalValue(form.getUnitValue() * form.getVolume());
        giftsApplicationDao.updateById(app);
        return app;
    }

    private GivingGiftsApplicationEntity saveGiftsApplication(Date currentDate,
                                                              UserExtensionEntity user,
                                                              GivingGiftsForm form) {
        log.info("save gifts application...");
        GivingGiftsApplicationEntity app = new GivingGiftsApplicationEntity();
        String reference = giftsDropDownService.getReference();
        BeanUtils.copyProperties(user,app);
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setSfUserIdCreator(user.getSfUserId());
        app.setSupervisorId(user.getSupervisorId());
        app.setEmployeeLe(user.getCompanyCode());
        app.setDepartment(user.getOrgTxt());
        app.setStatus(form.getActionType());
        app.setReasonType(form.getReasonType());
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setReference(reference);
        app.setGiftTypeId(0);
        app.setGiftTypeName("New Gift");
        app.setCreatedDate(currentDate);
        app.setLastModifiedDate(currentDate);
        app.setMarkDeleted(Constant.NO_EXIST_MARK);
        app.setDoctorId(0);
        app.setRequestType(Constant.GIVING_GIFTS_REQUEST_TYPE);
//        app.setRegion("");
        app.setTotalValue(form.getUnitValue() * form.getVolume());
//        if("Cultural Courtesy Gifts".equals(gift_Desc)&&!"0938".equals(emp_LE) ) {
//            gift_App.setIsUsed("Y");
//        } else {
//            gift_App.setIsUsed("N");
//        }
        app.setIsUsed(Constant.EXIST_MARK);
//        app.setDepartmentHeadId();
//        app.setDepartmentHeadName();
        giftsApplicationDao.insert(app);

        return app;
    }

}
