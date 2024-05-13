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
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.GivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.bayer.gifts.process.vo.TaskInstanceVo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public void updateGivingGifts(GivingGiftsForm form) {
        log.info("update giving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
//        mock begin >>>>>
//        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
//        mock end >>>>>
        GivingGiftsApplicationEntity application = updateGiftsApplication(currentDate,user, form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            Long userId = application.getSfUserIdAppliedFor();
            log.info("applicationId: {}", applicationId);
            updateGiftsActivity(currentDate, form);
            List<GiftsRelationPersonEntity> giftsPersonList =
                    giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,
                            userId, form.getFileId(), form.getVolume(),form.getUnitValue(),Constant.GIFTS_GIVING_TYPE);
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_GIVING_TYPE, form.getCopyToUserEmails(),user);
            List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            log.info("copy to user emails: {}", copyToUserEmails);
            GivingGiftsRefEntity giftsRef = updateGiftsRef(currentDate,applicationId,giftsPersonList, form);
            startProcess(application,user,giftsPersonList,giftsRef,copyToUserEmails, form);
        }
    }

    @Override
    @MasterTransactional
    public void saveGivingGifts(GivingGiftsForm form) {
        log.info("save giving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        //mock begin >>>>>
//        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
        //mock end >>>>>
        GivingGiftsApplicationEntity application = saveGiftsApplication(currentDate,user, form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info("applicationId: {}", applicationId);
        GivingGiftsActivityEntity activity = saveGiftsActivity(currentDate,application, form);
        List<GiftsRelationPersonEntity> giftsPersonList =
                giftsCompanyService.saveOrUpdateGiftsPerson(form.getCompanyList(),currentDate,applicationId,
                        userId, form.getFileId(),form.getVolume(), form.getUnitValue(),Constant.GIFTS_GIVING_TYPE);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_GIVING_TYPE, form.getCopyToUserEmails(),user);
        List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
        log.info("copy to user emails: {}", copyToUserEmails);
        GivingGiftsRefEntity giftsRef = saveGiftsRef(currentDate,applicationId,giftsPersonList, form);
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
            String processInstanceKey;
            String companyCode = user.getCompanyCode();
            if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
                processInstanceKey = Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX + user.getCompanyCode();
            }else if(Constant.GIFTS_LE_CODE_BCS_2614.equals(companyCode) ||
                    Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
                processInstanceKey = Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX + "2614_1391";
            }else {
                processInstanceKey = Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX + "0882_1954_1955";
            }
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
        GivingGiftsApplicationEntity application = giftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(application)){
            return null;
        }
        UserExtensionEntity user = userInfoService.getById(application.getSfUserIdAppliedFor());
        if(Objects.nonNull(user)){
            application.setSfUserAppliedName(user.getFirstName() + " " + user.getLastName());
            application.setSfUserAppliedEmail(user.getEmail());
            application.setSfUserAppliedCwid(user.getCwid());
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
            application.setFileAttach(fileAttach);
        }
        application.setGiftsRef(references);
        application.setCopyToUsers(copyToUsers);
        application.setGiftsActivities(giftsActivities);
        application.setCompanyList(companyList);
        return application;
    }



    @Override
    public Pagination<GivingGiftsApplicationEntity> getGivingGiftsApplicationList(GiftsApplicationParam param) {
        log.info("get giving gifts page...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
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
        variable.setGiftsPersonList(giftsPersonList);
        variable.setGivenPersons(giftsRef.getGivenPerson());
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
        variable.fillInExtraVar(user.getCompanyCode(),user.getBizGroup(), application.getDepartmentHeadId());

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
        app.setNewVersion(Constant.EXIST_MARK);
        // ToDo change fillIn logic
        fillInDepartmentHead(app);

        giftsApplicationDao.insert(app);

        return app;
    }


    private void fillInDepartmentHead(GivingGiftsApplicationEntity app) {
        log.info("fillIn department head information...");
        String companyCode = app.getEmployeeLe();
        GiftsGroupEntity departmentHeadGroup = Constant.GIFTS_GROUP_MAP.get("DEPARTMENT_HEAD_" + companyCode);
        if(Objects.nonNull(departmentHeadGroup) && CollectionUtils.isNotEmpty(departmentHeadGroup.getUserToGroups())){
            //默认选择第一个
            GiftsUserToGroupEntity departmentUser = departmentHeadGroup.getUserToGroups().get(0);
            app.setDepartmentHeadId(departmentUser.getUserId());
            app.setDepartmentHeadName(departmentUser.getUserFirstName() + " " + departmentUser.getUserLastName());
            log.info("department head id: {}",app.getDepartmentHeadId());
            log.info("department head name: {}", app.getDepartmentHeadName());
        }
    }

}
