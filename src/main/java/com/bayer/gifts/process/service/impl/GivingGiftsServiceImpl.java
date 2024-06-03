package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.GivingGiftsActivityDao;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.dao.GivingGiftsRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("givingGiftsService")
public class GivingGiftsServiceImpl implements GivingGiftsService {

    @Autowired
    StorageService storageService;
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
    @Autowired
    GiftsBaseService giftsBaseService;


    @Override
    @MasterTransactional
    public Long copyGivingGifts(Long application) {
        log.info("copy giving gifts...");
        return giftsBaseService.copyGiftsRecord(application,Constant.GIFTS_GIVING_TYPE);
    }

    @Override
    @MasterTransactional
    public void updateGivingGifts(GivingGiftsForm form) {
        log.info("update giving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GivingGiftsApplicationEntity application = updateGiftsApplication(currentDate,user, form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            Long userId = application.getSfUserIdAppliedFor();
            log.info("applicationId: {}", applicationId);
            updateGiftsActivity(currentDate, form);
            List<GiftsRelationPersonEntity> giftsPersonList =
                    giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,
                            userId, form.getFileId(),Constant.GIFTS_GIVING_TYPE);
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_GIVING_TYPE, form.getCopyToUserEmails(),user);
            List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            log.info("copy to user emails: {}", copyToUserEmails);
            GivingGiftsRefEntity giftsRef = updateGiftsRef(currentDate,applicationId,user,giftsPersonList, form);
            startProcess(application,user,giftsPersonList,giftsRef,copyToUserEmails, form);
        }
    }

    @Override
    @MasterTransactional
    public void saveGivingGifts(GivingGiftsForm form) {
        log.info("save giving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GivingGiftsApplicationEntity application = saveGiftsApplication(currentDate,user, form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info("applicationId: {}", applicationId);
        GivingGiftsActivityEntity activity = saveGiftsActivity(currentDate,application, form);
        List<GiftsRelationPersonEntity> giftsPersonList =
                giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,
                        userId, form.getFileId(),Constant.GIFTS_GIVING_TYPE);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_GIVING_TYPE, form.getCopyToUserEmails(),user);
        List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
        log.info("copy to user emails: {}", copyToUserEmails);
        GivingGiftsRefEntity giftsRef = saveGiftsRef(currentDate,applicationId,user,giftsPersonList, form);
        startProcess(application,user,giftsPersonList,giftsRef,copyToUserEmails, form);
    }

    @Override
    @MasterTransactional
    public void deleteDraftGivingGifts(Long applicationId) {
        log.info("delete giving gifts...");
        GivingGiftsApplicationEntity app = giftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            log.info("empty giving gifts application...");
            return;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("giving gifts status: {} , can not be change", app.getStatus());
            return;
        }
        log.info("delete giving gifts application...");
        giftsApplicationDao.deleteById(applicationId);
        log.info("delete giving gifts ref...");
        givingGiftsRefDao.delete(Wrappers.<GivingGiftsRefEntity>lambdaQuery()
                .eq(GivingGiftsRefEntity::getApplicationId, applicationId));
        log.info("delete giving gifts activity...");
        givingGiftsActivityDao.delete(Wrappers.<GivingGiftsActivityEntity>lambdaQuery()
                .eq(GivingGiftsActivityEntity::getApplicationId, applicationId));
        log.info("delete giving gifts relation person...");
        giftsCompanyService.deleteGiftsRelationPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,Constant.GIFTS_GIVING_TYPE);
        giftsCopyToService.remove(Wrappers.<GiftsCopyToEntity>lambdaQuery()
                .eq(GiftsCopyToEntity::getApplicationId, applicationId)
                .eq(GiftsCopyToEntity::getType, Constant.GIFTS_GIVING_TYPE));
    }



    @Override
    @MasterTransactional
    public void cancelGivingGifts(GivingGiftsForm form) {
        log.info("cancel giving gifts...");
        giftsBaseService.cancelGiftsProcess(form,Constant.GIFTS_GIVING_TYPE);
    }

    private void startProcess(GivingGiftsApplicationEntity application, UserExtensionEntity user,
                              List<GiftsRelationPersonEntity> giftsPersonList,
                              GivingGiftsRefEntity giftsRef, List<String> copyToUserEmails, GivingGiftsForm form) {
        if(Constant.GIFT_SUBMIT_TYPE.equals(form.getActionType())){
            log.info("start giving gifts process...");
            Map<String, Object> variables = Maps.newHashMap();
            GivingGiftsApplyVariable applyVar = copyInforToApplyVar(user,application,giftsPersonList,giftsRef,copyToUserEmails);
            variables.put(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE, applyVar);
            //String processDefinitionKey, String businessKey, Map<String, Object> variables
            String processInstanceKey = giftsBaseService.getProcessInstanceKey(user, Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX);
//            giftsBaseService.copyToGiftsProcess(applyVar,Constant.GIFTS_GIVING_TYPE);
            ProcessInstance  processInstance =
                    runtimeService.startProcessInstanceByKey(processInstanceKey,
                            String.valueOf(application.getApplicationId()), variables);
            Long processId = Long.valueOf(processInstance.getId());
            log.info("Process instance id >>>>> {}", processId);
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
        GivingGiftsApplicationEntity app = giftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            return null;
        }
        UserExtensionEntity applyForUser = userInfoService.getById(app.getSfUserIdAppliedFor());
        if(Objects.nonNull(applyForUser)){
            String userEmail = applyForUser.getEmail();
            app.setSfUserAppliedFirstName(applyForUser.getFirstName());
            app.setSfUserAppliedLastName(applyForUser.getLastName());
            app.setSfUserAppliedName(applyForUser.getFirstName() + " " + applyForUser.getLastName());
            app.setSfUserAppliedEmail(userEmail);
            app.setSfUserAppliedCwid(applyForUser.getCwid());
        }

        if(Objects.nonNull(app.getDepartmentHeadId())){
            log.info("add department head information...");
            GiftsUserToGroupEntity  deptHeadUser =
                 Constant.GIFTS_GROUP_MAP.entrySet().stream()
                         .flatMap(g -> g.getValue().getUserToGroups().stream())
                         .filter(g -> g.getUserId().equals(app.getDepartmentHeadId())).findFirst().orElse(null);
            if(Objects.nonNull(deptHeadUser)){
                GiftsGroupEntity deptHeadGroup = new GiftsGroupEntity();
                deptHeadGroup.setUserToGroups(Collections.singletonList(deptHeadUser));
                app.setDeptHeadGroup(deptHeadGroup);
            }
        }
        GivingGiftsRefEntity references = givingGiftsRefDao.selectOne(Wrappers.<GivingGiftsRefEntity>lambdaQuery().
                eq(GivingGiftsRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,Constant.GIFTS_GIVING_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,
                Constant.GIFTS_GIVING_TYPE);
        log.info("Company size: {}", companyList.size());
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<GivingGiftsActivityEntity> giftsActivities =
                giftsApplicationDao.queryGivingGiftsActivityList(activityParam);
        log.info("giftsActivities size: {}", giftsActivities.size());
        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,Constant.GIFTS_GIVING_TYPE,"CompanyPerson");
        if(Objects.nonNull(fileAttach)){
            log.info("gifts file attachment: {}", fileAttach.getFileName());
            app.setFileAttach(fileAttach);
        }
        app.setGiftsRef(references);
        app.setCopyToUsers(copyToUsers);
        app.setGiftsActivities(giftsActivities);
        app.setCompanyList(companyList);
        return app;
    }



    @Override
    public Pagination<GivingGiftsApplicationEntity> getGivingGiftsApplicationList(GiftsApplicationParam param) {
        log.info("get giving gifts page...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        if(CollectionUtils.isEmpty(param.getOrders())){
            log.info("default order by GIVEN_DATE...");
          param.setOrders(Collections.singletonList(OrderByParam.builder().column("GIVEN_DATE").type("DESC").build()));
        }
        IPage<GivingGiftsApplicationEntity> page = giftsApplicationDao.queryGivingGiftsApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }



    private GivingGiftsApplyVariable copyInforToApplyVar(UserExtensionEntity user,
                                                         GivingGiftsApplicationEntity application,
                                                         List<GiftsRelationPersonEntity> giftsPersonList,
                                                         GivingGiftsRefEntity giftsRef,
                                                         List<String> copyToUserEmails) {
        GivingGiftsApplyVariable variable = new GivingGiftsApplyVariable();
        BeanUtils.copyProperties(user,variable);
        BeanUtils.copyProperties(application,variable);
        BeanUtils.copyProperties(giftsRef,variable);
        variable.setActionType(Constant.GIFT_SUBMIT_TYPE);
        variable.setTotalValue(application.getTotalValue());
//        variable.setTotalValue(giftsRef.getUnitValue() * giftsRef.getVolume());
        variable.setApplyDate(DateUtils.dateToStr(
                application.getCreatedDate(), DateUtils.DATE_PATTERN));
        variable.setCreatorId(application.getSfUserIdCreator());
        variable.setCreatorName(user.getFirstName() + " " + user.getLastName());
        variable.setCreatorEmail(user.getEmail());
        variable.setGivenDate(DateUtils.dateToStr(
                giftsRef.getGivenDate(), DateUtils.DATE_PATTERN));
        UserExtensionEntity applyForUser = application.getApplyForUser();
        variable.setApplyForId(applyForUser.getSfUserId());
        variable.setApplyForName(applyForUser.getFirstName() + " " + applyForUser.getLastName());
        variable.setApplyEmail(applyForUser.getEmail());
        variable.setGiftsPersonList(giftsPersonList);
        variable.setGivenPersons(giftsRef.getGivenPerson());
        variable.setReferenceNo(application.getReference());
        variable.setApplicationId(application.getApplicationId());
        if(CollectionUtils.isNotEmpty(copyToUserEmails)){
            variable.setCopyToUserEmails(copyToUserEmails);
        }
        UserExtensionEntity supervisor = applyForUser.getSupervisor();
        if(Objects.nonNull(supervisor)){
            variable.setSupervisorId(supervisor.getSfUserId());
            variable.setSupervisorName(supervisor.getFirstName() + " " + supervisor.getLastName());
            variable.setSupervisorMail(supervisor.getEmail());
        }
        variable.fillInExtraVar(user.getCompanyCode(),user.getBizGroup(), application.getDeptHeadGroup());

        return variable;
    }





    private GivingGiftsRefEntity updateGiftsRef(Date currentDate, Long applicationId,UserExtensionEntity user,
                                                List<GiftsRelationPersonEntity> giftsPersonList, GivingGiftsForm form) {
        log.info("update giving gifts reference...");
        GivingGiftsRefEntity giftsRef = givingGiftsRefDao.selectOne(Wrappers.<GivingGiftsRefEntity>lambdaQuery()
                .eq(GivingGiftsRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return null;
        }
        String givenPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givenCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName)
                .collect(Collectors.joining(","));
        log.info("give persons: {}", givenPersons);
        log.info("give company: {}", givenCompany);
        BeanUtils.copyProperties(form, giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setGivenPerson(givenPersons);
        giftsRef.setGivenCompany(givenCompany);
        giftsRef.setGivenDate(form.getDate());
        giftsRef.setLastModifiedDate(currentDate);
        fillInMultiLanguageOfGiftsRef(giftsRef,user);
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

    private GivingGiftsRefEntity saveGiftsRef(Date currentDate, Long applicationId, UserExtensionEntity user,
                                              List<GiftsRelationPersonEntity> giftsPersonList, GivingGiftsForm form) {
        log.info("save giving gifts reference...");
        String givenPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givenCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName)
                .collect(Collectors.joining(","));

        log.info("give persons: {}", givenPersons);
        log.info("give company: {}", givenCompany);
        GivingGiftsRefEntity giftsRef = new GivingGiftsRefEntity();
        BeanUtils.copyProperties(form, giftsRef);
        giftsRef.setApplicationId(applicationId);
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
        fillInMultiLanguageOfGiftsRef(giftsRef,user);
//            giftsRef.setGivingDate();
//            giftsRef.setAttachmentFile();
//            giftsRef.setAttachmentFileName();
//            giftsRef.setCategoryId();
//            giftsRef.setCategoryName();
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
//        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        String applyForEmail = Objects.isNull(form.getApplyName()) ? user.getEmail() : form.getApplyName();
        if(!applyForEmail.equals(user.getEmail())){
            UserExtensionEntity applyForUser = userInfoService.getUserInfo(applyForEmail,false,false);
            giftsBaseService.fillInApplyForUser(applyForUser,app);
        }else {
            giftsBaseService.fillInApplyForUser(user,app);
        }
        app.setStatus(form.getActionType());
        app.setReasonType(form.getReasonType());
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setLastModifiedDate(currentDate);
//        app.setTotalValue(form.getUnitValue() * form.getVolume());
        app.setTotalValue(form.getTotalValue());
        fillInMultiLanguageOfApp(app);
        giftsBaseService.fillInDepartmentHead(app,form,user.getDivision());
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
        String applyForEmail = Objects.isNull(form.getApplyName()) ? user.getEmail() : form.getApplyName();
        if(!applyForEmail.equals(user.getEmail())){
            UserExtensionEntity applyForUser = userInfoService.getUserInfo(applyForEmail,false,false);
            giftsBaseService.fillInApplyForUser(applyForUser,app);
        }else {
            giftsBaseService.fillInApplyForUser(user,app);
        }
        app.setSfUserIdCreator(user.getSfUserId());
//        app.setSupervisorId(user.getSupervisorId());
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
//        app.setTotalValue(form.getUnitValue() * form.getVolume());
        app.setTotalValue(form.getTotalValue());
//        if("Cultural Courtesy Gifts".equals(gift_Desc)&&!"0938".equals(emp_LE) ) {
//            gift_App.setIsUsed("Y");
//        } else {
//            gift_App.setIsUsed("N");
//        }
        app.setIsUsed(Constant.NO_EXIST_MARK);
        app.setNewVersion(Constant.EXIST_MARK);
        fillInMultiLanguageOfApp(app);
        giftsBaseService.fillInDepartmentHead(app,form,user.getDivision());
        giftsApplicationDao.insert(app);
        return app;
    }

    private void fillInMultiLanguageOfApp(GivingGiftsApplicationEntity app) {
        log.info("begin fill in multi language in application...");
        List<GiftsDictionaryEntity> reasonTypeCNList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_REASON_TYPE, Constant.GIFTS_LANGUAGE_CN));
        List<GiftsDictionaryEntity> reasonTypeENList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_REASON_TYPE, Constant.GIFTS_LANGUAGE_EN));
        String reasonType = app.getReasonType();

        String reasonTypeCN =  reasonTypeCNList.stream().
                filter(s -> s.getCode().equals(reasonType)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        String reasonTypeEN = reasonTypeENList.stream().
                filter(s -> s.getCode().equals(reasonType)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        log.info(">>>> reasonType: {}, reasonTypeCN: {}, reasonTypeEN: {}",
                reasonType, reasonTypeCN, reasonTypeEN);
        app.setReasonTypeCN(reasonTypeCN);
        app.setReasonTypeEN(reasonTypeEN);
    }


    private void fillInMultiLanguageOfGiftsRef(GivingGiftsRefEntity giftsRef,UserExtensionEntity user) {
        log.info("begin fill in multi language in ref...");
        String companyCode = user.getCompanyCode();
        String division = user.getDivision();
        List<GiftsDictionaryEntity> isGoSocDictCNList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_IS_GO_SOC, Constant.GIFTS_LANGUAGE_CN));
        List<GiftsDictionaryEntity> isGoSocDictENList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_IS_GO_SOC, Constant.GIFTS_LANGUAGE_EN));

        List<GiftsDictionaryEntity> isBayerCustomerCNList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_IS_BAYER_CUSTOMER, Constant.GIFTS_LANGUAGE_CN));
        List<GiftsDictionaryEntity> isBayerCustomerENList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_IS_BAYER_CUSTOMER, Constant.GIFTS_LANGUAGE_EN));

        List<GiftsDictionaryEntity> giftDescTypeCNList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_GIFT_DESC_TYPE, Constant.GIFTS_LANGUAGE_CN));
        List<GiftsDictionaryEntity> giftDescTypeENList =
                Constant.GIFTS_DICT_MAP.get(Pair.of(Constant.GIFTS_DICT_GIFT_DESC_TYPE, Constant.GIFTS_LANGUAGE_EN));

        String isGoSoc = giftsRef.getIsGoSoc();
        String isBayerCustomer = giftsRef.getIsBayerCustomer();
        String giftDescType = giftsRef.getGiftDescType();

        String isGoScoNameCN =  isGoSocDictCNList.stream().
                filter(s -> s.getCode().equals(isGoSoc)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        String isGoScoNameEN = isGoSocDictENList.stream().
                filter(s -> s.getCode().equals(isGoSoc)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        log.info(">>>> isGoSoc: {}, isGoScoNameCN: {}, isGoScoNameEN: {}",
                isGoSoc, isGoScoNameCN, isGoScoNameEN);

        String isBayerCustomerCN =  isBayerCustomerCNList.stream().
                filter(s -> s.getCode().equals(isBayerCustomer)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        String isBayerCustomerEN = isBayerCustomerENList.stream().
                filter(s -> s.getCode().equals(isBayerCustomer)).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(StringUtils.EMPTY);
        log.info(">>>> isBayerCustomer: {}, isBayerCustomerCN: {}, isBayerCustomerEN: {}",
                isBayerCustomer, isBayerCustomerCN, isBayerCustomerEN);

        String giftDescTypeCN;
        String giftDescTypeEN;
        StringBuilder companyKey = new StringBuilder(StringUtils.EMPTY);
        if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
            companyKey.append(companyCode);
        }else if(Constant.GIFTS_LE_CODE_BCS_2614.equals(companyCode) || Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
            companyKey.append("2614_1391");
        }else if(Constant.GIFTS_LE_CODE_BHC_0882.equals(companyCode) && "CH".equals(division)){
            companyKey.append(companyCode).append("_").append(division);
        }
        giftDescTypeCN = giftDescTypeCNList.stream().
                filter(s -> Constant.GIFTS_GIVING_TYPE.equals(s.getModule()) && s.getCode().equals(giftDescType)
                        && companyKey.toString().equals(s.getCompany())).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(giftDescType);
        giftDescTypeEN = giftDescTypeENList.stream().
                filter(s -> Constant.GIFTS_GIVING_TYPE.equals(s.getModule()) && s.getCode().equals(giftDescType)
                        && companyKey.toString().equals(s.getCompany())).map(GiftsDictionaryEntity::getName)
                .findFirst().orElse(giftDescType);
        log.info(">>>> giftDescType: {}, giftDescTypeCN: {}, giftDescTypeEN: {}",
                giftDescType, giftDescTypeCN, giftDescTypeEN);

        giftsRef.setIsGoSocNameCN(isGoScoNameCN);
        giftsRef.setIsGoSocNameEN(isGoScoNameEN);

        giftsRef.setIsBayerCustomerCN(isBayerCustomerCN);
        giftsRef.setIsBayerCustomerEN(isBayerCustomerEN);

        giftsRef.setGiftDescTypeCN(giftDescTypeCN);
        giftsRef.setGiftDescTypeEN(giftDescTypeEN);
    }

}
