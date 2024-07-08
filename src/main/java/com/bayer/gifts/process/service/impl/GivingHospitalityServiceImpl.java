package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.GivingHospitalityActivityDao;
import com.bayer.gifts.process.dao.GivingHospitalityApplicationDao;
import com.bayer.gifts.process.dao.GivingHospitalityRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GivingHospitalityFrom;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GivingHospApplyVariable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("givingHospitalityService")
public class GivingHospitalityServiceImpl implements GivingHospitalityService {

    @Autowired
    @Qualifier("threadExecutor")
    ThreadPoolTaskExecutor threadExecutor;
    @Autowired
    StorageService storageService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    GiftsCopyToService giftsCopyToService;
    @Autowired
    GiftsDropDownService giftsDropDownService;
    @Autowired
    GiftsCompanyService giftsCompanyService;
    @Autowired
    GivingHospitalityApplicationDao hospitalityApplicationDao;
    @Autowired
    GivingHospitalityActivityDao hospitalityActivityDao;
    @Autowired
    GivingHospitalityRefDao hospitalityRefDao;

    @Autowired
    GiftsBaseService giftsBaseService;

    @Override
    @MasterTransactional
    public Long copyGivingHospitality(Long application) {
        log.info("copy giving hospitality...");
        return giftsBaseService.copyGiftsRecord(application,Constant.HOSPITALITY_TYPE);
    }

    @Override
    @MasterTransactional
    public void updateGivingHospitality(GivingHospitalityFrom form) {
        log.info("update giving hospitality...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GivingHospApplicationEntity application = updateHospitalityApplication(currentDate,user,form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            Long userId = application.getSfUserIdAppliedFor();
            log.info(">>>>>>>>>> applicationId: {} referenceNo: {}", applicationId, application.getReference());
            updateHospitalityActivity(currentDate, form);
            List<GivingHospRelationPersonEntity> hospPersonList =
                    giftsCompanyService.saveOrUpdateHospPerson(form.getCompanyList(),user,currentDate,applicationId,
                            form.getExpensePerHead(),
                            form.getFileId(),Constant.GIFTS_GIVING_TYPE);
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.HOSPITALITY_TYPE,
                            form.getCopyToUserEmails(),user);
            List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            log.info("copy to user emails: {}", copyToUserEmails);
            GivingHospRefEntity hospRef = updateHospRef(currentDate,applicationId,form);
            storageService.saveFileAttach(currentDate,applicationId,userId,form.getExtraFileIds());
//            startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form);
            threadExecutor.execute(() -> startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form));
        }
    }

    @Override
    @MasterTransactional
    public void saveGivingHospitality(GivingHospitalityFrom form) {
        log.info("save giving hospitality...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GivingHospApplicationEntity application = saveHospitalityApplication(currentDate,user,form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info(">>>>>>>>>> applicationId: {} referenceNo: {}", applicationId, application.getReference());
        saveHospitalityActivity(currentDate,application,form);
        List<GivingHospRelationPersonEntity> hospPersonList =
                giftsCompanyService.saveOrUpdateHospPerson(form.getCompanyList(),user,currentDate,applicationId,
                        form.getExpensePerHead(),
                        form.getFileId(),Constant.GIFTS_GIVING_TYPE);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.HOSPITALITY_TYPE,
                        form.getCopyToUserEmails(),user);
        List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
        log.info("copy to user emails: {}", copyToUserEmails);
        GivingHospRefEntity hospRef = saveHospRef(currentDate,applicationId, form);
        storageService.saveFileAttach(currentDate,applicationId,userId,form.getExtraFileIds());
//        startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form);
        threadExecutor.execute(() -> startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form));
    }


    private void startProcess(GivingHospApplicationEntity application,
                              UserExtensionEntity user,
                              GivingHospRefEntity hospRef,
                              List<GivingHospRelationPersonEntity> hospPersonList,
                              List<String> copyToUserEmails, GivingHospitalityFrom from) {
        if(Constant.GIFT_SUBMIT_TYPE.equals(from.getActionType())){
            log.info("start giving hospitality process...");
            Map<String, Object> variables = Maps.newHashMap();
            GivingHospApplyVariable applyVar = copyInforToApplyVar(user,application,hospPersonList,hospRef,copyToUserEmails);
            variables.put(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE, applyVar);
            UserExtensionEntity applyForUser = Objects.isNull(application.getApplyForUser()) ? user : application.getApplyForUser();
            log.info(">>>>> user cwid: {}, applyForUser cwid: {}", user.getCwid(), applyForUser.getCwid());
            if(applyVar.isUnGocAndLessThan300()){
                log.info("unGovernment and expensePerHead lessThan 300, applicationId >>>>> {}",application.getApplicationId());
                GiftsActivityBaseEntity lastOneActivity =
                        giftsBaseService.getGiftsActivityBaseLastOne(application.getApplicationId(),Constant.HOSPITALITY_TYPE);
                giftsBaseService.updateAndProcessBusiness(application,lastOneActivity,variables,Constant.GIFTS_DOCUMENTED_TYPE,
                        Constant.HOSPITALITY_TYPE,Constant.GIFTS_DOCUMENTED_TYPE,Constant.GIFTS_DOCUMENTED_TYPE,true);
                return;
            }
            //String processDefinitionKey, String businessKey, Map<String, Object> variables
            String processInstanceKey = giftsBaseService.getProcessInstanceKey(applyForUser, Constant.GIVING_HOSP_PROCESS_TYPE_PREFIX);
//            giftsBaseService.copyToGiftsProcess(applyVar,Constant.HOSPITALITY_TYPE);
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceByKey(processInstanceKey,
                            String.valueOf(application.getApplicationId()), variables);
            Long processId = Long.valueOf(processInstance.getId());
            log.info("Process instance id >>>>> {}", processId);
            log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());

            hospitalityApplicationDao.update(null, Wrappers.<GivingHospApplicationEntity>lambdaUpdate()
                    .set(GivingHospApplicationEntity::getSfProcessInsId, processId)
                    .eq(GivingHospApplicationEntity::getApplicationId,application.getApplicationId()));
            hospitalityActivityDao.update(null, Wrappers.<GivingHospActivityEntity>lambdaUpdate()
                    .set(GivingHospActivityEntity::getSfProcessInsId, processId)
                    .eq(GivingHospActivityEntity::getApplicationId,application.getApplicationId()));
        }
    }

    private GivingHospApplyVariable copyInforToApplyVar(UserExtensionEntity user,
                                                        GivingHospApplicationEntity application,
                                                        List<GivingHospRelationPersonEntity> hospPersonList,
                                                        GivingHospRefEntity hospRef,
                                                        List<String> copyToUserEmails) {
        GivingHospApplyVariable variable = new GivingHospApplyVariable();
        BeanUtils.copyProperties(user,variable);
        BeanUtils.copyProperties(application,variable);
        BeanUtils.copyProperties(hospRef,variable);
        variable.setActionType(Constant.GIFT_SUBMIT_TYPE);
//        variable.setEstimatedTotalExpense(application.getEstimatedTotalExpense());
        variable.setApplyDate(DateUtils.dateToStr(
                application.getCreatedDate(), DateUtils.DATE_PATTERN));
        variable.setCreatorId(application.getSfUserIdCreator());
        variable.setCreatorName(user.getFirstName() + " " + user.getLastName());
        variable.setCreatorEmail(user.getEmail());
        variable.setHospitalityDate(DateUtils.dateToStr(
                hospRef.getHospitalityDate(), DateUtils.DATE_PATTERN));
        UserExtensionEntity applyForUser = application.getApplyForUser();
        variable.setApplyForId(applyForUser.getSfUserId());
        variable.setApplyForName(applyForUser.getFirstName() + " " + applyForUser.getLastName());
        variable.setApplyEmail(applyForUser.getEmail());

        variable.setHospPersonList(hospPersonList);
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


    private GivingHospRefEntity updateHospRef(Date currentDate, Long applicationId, GivingHospitalityFrom form) {
        log.info("update giving hospitality reference...");
        GivingHospRefEntity giftsRef = hospitalityRefDao.selectOne(Wrappers.<GivingHospRefEntity>lambdaQuery()
                .eq(GivingHospRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return null;
        }
        BeanUtils.copyProperties(form, giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setHospitalityDate(form.getDate());
        giftsRef.setLastModifiedDate(currentDate);
        hospitalityRefDao.updateById(giftsRef);
        return giftsRef;
    }

    private GivingHospRefEntity saveHospRef(Date currentDate, Long applicationId, GivingHospitalityFrom form) {
        log.info("save giving hospitality reference...");
        GivingHospRefEntity giftsRef = new GivingHospRefEntity();
        BeanUtils.copyProperties(form, giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setHospitalityDate(form.getDate());
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
        hospitalityRefDao.insert(giftsRef);
        return giftsRef;
    }


    private GivingHospActivityEntity saveHospitalityActivity(Date currentDate, GivingHospApplicationEntity application,
                                                             GivingHospitalityFrom form) {
        log.info("save giving hospitality activity...");
        GivingHospActivityEntity activity = new GivingHospActivityEntity();
        activity.setApplicationId(application.getApplicationId());
        activity.setSfUserIdSubmitter(application.getSfUserIdCreator());
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        hospitalityActivityDao.insert(activity);
        return activity;
    }

    private GivingHospActivityEntity updateHospitalityActivity(Date currentDate, GivingHospitalityFrom form) {
        log.info("update giving hospitality activity...");
        GivingHospActivityEntity activity = hospitalityActivityDao.selectOne(Wrappers.<GivingHospActivityEntity>lambdaQuery().
                eq(GivingHospActivityEntity::getApplicationId,form.getApplicationId()));
        if(Objects.isNull(activity)){
            log.info("empty giving hospitality activity...");
            return null;
        }
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setLastModifiedDate(currentDate);
        hospitalityActivityDao.updateById(activity);
        return activity;
    }


    private GivingHospApplicationEntity updateHospitalityApplication(Date currentDate,
                                                                     UserExtensionEntity user,
                                                                     GivingHospitalityFrom form) {
        log.info("update giving hospitality application...");
        GivingHospApplicationEntity app = hospitalityApplicationDao.selectById(form.getApplicationId());
        if(Objects.isNull(app)){
            log.info("empty giving hospitality application...");
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
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setLastModifiedDate(currentDate);
        app.setEstimatedTotalExpense(form.getEstimatedTotalExpense());
        giftsBaseService.fillInDepartmentHead(app,form,user.getDivision());
        hospitalityApplicationDao.updateById(app);
        return app;

    }

    private GivingHospApplicationEntity saveHospitalityApplication(Date currentDate,
                                                                   UserExtensionEntity user,
                                                                   GivingHospitalityFrom form) {
        log.info("save hospitality application...");
        GivingHospApplicationEntity app = new GivingHospApplicationEntity();
        String reference = giftsDropDownService.getReference();
        BeanUtils.copyProperties(user,app);
//        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
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
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setReference(reference);
//        app.setGiftTypeId(0);
//        app.setGiftTypeName("New Gift");
        app.setCreatedDate(currentDate);
        app.setLastModifiedDate(currentDate);
        app.setMarkDeleted(Constant.NO_EXIST_MARK);
//        app.setRegion("");
        app.setEstimatedTotalExpense(form.getEstimatedTotalExpense());
//        if("Cultural Courtesy Gifts".equals(gift_Desc)&&!"0938".equals(emp_LE) ) {
//            gift_App.setIsUsed("Y");
//        } else {
//            gift_App.setIsUsed("N");
//        }
        app.setIsUsed(Constant.NO_EXIST_MARK);
        app.setNewVersion(Constant.EXIST_MARK);
        giftsBaseService.fillInDepartmentHead(app,form,user.getDivision());
        hospitalityApplicationDao.insert(app);
        return app;
    }



    @Override
    @MasterTransactional
    public void cancelGivingHospitality(GivingHospitalityFrom form) {
        log.info("cancel giving hospitality...");
        giftsBaseService.cancelGiftsProcess(form,Constant.HOSPITALITY_TYPE);
    }

    @Override
    @MasterTransactional
    public void deleteDraftGivingHospitality(Long applicationId) {
        log.info("delete giving hospitality...");
        GivingHospApplicationEntity app = hospitalityApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            log.info("empty giving hospitality gifts application...");
            return;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("receiving gifts status: {} , can not be change", app.getStatus());
            return;
        }
        log.info("delete giving hospitality application...");
        hospitalityApplicationDao.deleteById(applicationId);
        log.info("delete giving hospitality ref...");
        hospitalityRefDao.delete(Wrappers.<GivingHospRefEntity>lambdaQuery()
                .eq(GivingHospRefEntity::getApplicationId, applicationId));
        log.info("delete giving hospitality activity...");
        hospitalityActivityDao.delete(Wrappers.<GivingHospActivityEntity>lambdaQuery()
                .eq(GivingHospActivityEntity::getApplicationId, applicationId));
        log.info("delete giving hospitality relation person...");
        giftsCompanyService.deleteGiftsRelationPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,Constant.GIFTS_GIVING_TYPE);
        giftsCopyToService.remove(Wrappers.<GiftsCopyToEntity>lambdaQuery()
                .eq(GiftsCopyToEntity::getApplicationId, applicationId)
                .eq(GiftsCopyToEntity::getType, Constant.GIFTS_GIVING_TYPE));
    }

    @Override
    public GivingHospApplicationEntity getGivingHospitalityByApplicationId(Long applicationId) {
        log.info("get giving hospitality...");
        log.info("get giving hospitality: {}",applicationId);
        GivingHospApplicationEntity app = hospitalityApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            return null;
        }
        giftsBaseService.fillInUserInfo(app);
        giftsBaseService.fillInDepartmentHead(app);
        giftsBaseService.fillInCountryHead(app);
        GivingHospRefEntity references = hospitalityRefDao.selectOne(Wrappers.<GivingHospRefEntity>lambdaQuery().
                eq(GivingHospRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,Constant.HOSPITALITY_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,Constant.HOSPITALITY_TYPE,
                Constant.GIFTS_GIVING_TYPE);
        log.info("Company size: {}", companyList.size());
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<GivingHospActivityEntity> hospActivities =
                hospitalityApplicationDao.queryGivingHospitalityActivityList(activityParam);
        log.info("giftsActivities size: {}", hospActivities.size());
        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,Constant.HOSPITALITY_TYPE,
                Constant.COMPANY_PERSON_ATTACH_MODULE);
        if(Objects.nonNull(fileAttach)){
            log.info("giving hospitality file attachment: {}", fileAttach.getFileName());
            app.setFileAttach(fileAttach);
        }
        List<FileUploadEntity> extraFileAttach = storageService.getUploadFiles(applicationId, Constant.HOSPITALITY_TYPE,
                Constant.EXTRA_ATTACH_MODULE);
        if(CollectionUtils.isNotEmpty(extraFileAttach)){
            log.info("giving hospitality file extra attachment size: {}", extraFileAttach.size());
            app.setExtraAttachments(extraFileAttach);
        }

        app.setHospRef(references);
        app.setCopyToUsers(copyToUsers);
        app.setHospActivities(hospActivities);
        app.setCompanyList(companyList);
        return app;
    }

    @Override
    public GivingHospApplicationEntity getGivingHospitalityHistoryByApplicationId(Long applicationId) {
        log.info("get giving history hospitality...");
        log.info("get giving history hospitality: {}",applicationId);
        GivingHospApplicationEntity app = hospitalityApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            return null;
        }
        giftsBaseService.fillInUserInfo(app);
        giftsBaseService.fillInDepartmentHead(app);
        giftsBaseService.fillInCountryHead(app);
        GivingHospRefEntity references = hospitalityRefDao.selectOne(Wrappers.<GivingHospRefEntity>lambdaQuery().
                eq(GivingHospRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,Constant.HOSPITALITY_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getHisComPersonByApplicationId(applicationId);
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
//        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,Constant.HOSPITALITY_TYPE,"History");
//        if(Objects.nonNull(fileAttach)){
//            log.info("gifts file attachment: {}", fileAttach.getFileName());
//            app.setFileAttach(fileAttach);
//        }
        List<GivingHospActivityEntity> hospActivities =
                hospitalityApplicationDao.queryGivingHospitalityActivityList(activityParam);
        if(StringUtils.isNotEmpty(app.getAttachmentFile())){
            FileUploadEntity fileAttach = new FileUploadEntity();
            fileAttach.setId(new Random().nextLong());
            fileAttach.setFileName(app.getAttachmentFile());
            fileAttach.setOrigFileName(app.getAttachmentFile());
            fileAttach.setFilePath(app.getAttachmentFileName());
            app.setFileAttach(fileAttach);
        }

        app.setHospRef(references);
        app.setCopyToUsers(copyToUsers);
        app.setCompanyList(companyList);
        app.setHospActivities(hospActivities);
        return app;
    }

    @Override
    public Pagination<GivingHospApplicationEntity> getGivingHospitalityApplicationList(GiftsApplicationParam param) {
        log.info("page giving hospitality...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        if(CollectionUtils.isEmpty(param.getOrders())){
            log.info("default order by HOSPITALITY_DATE...");
            param.setOrders(Collections.singletonList(OrderByParam.builder().column("HOSPITALITY_DATE").type("DESC").build()));
        }
        GiftsUserToGroupEntity socGroupUser =
                Constant.GIFTS_GROUP_MAP.entrySet().stream()
                        .filter(g -> g.getKey().endsWith(Constant.GIFTS_LEADERSHIP_SOC_GROUP))
                        .flatMap(g -> g.getValue().getUserToGroups().stream())
                        .filter(u -> u.getUserId().equals(user.getSfUserId())).findFirst().orElse(null);
        if(Objects.nonNull(socGroupUser)){
            param.setIsSCOPartner(true);
        }
        log.info("is soc partner >>>> {}",param.getIsSCOPartner());
        IPage<GivingHospApplicationEntity> page = hospitalityApplicationDao.queryGivingHospitalityApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }
}
