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
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("givingHospitalityService")
public class GivingHospitalityServiceImpl implements GivingHospitalityService {

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

    @Override
    @MasterTransactional
    public void updateGivingHospitality(GivingHospitalityFrom form) {
        log.info("update giving hospitality...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        HospitalityApplicationEntity application = updateHospitalityApplication(currentDate,user,form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            Long userId = application.getSfUserIdAppliedFor();
            log.info("applicationId: {}", applicationId);
            HospitalityActivityEntity activity = updateHospitalityActivity(currentDate, form);
            List<HospitalityRelationPersonEntity> hospPersonList =
                    giftsCompanyService.saveOrUpdateHospPerson(form.getCompanyList(),currentDate,applicationId,
                            userId, form.getFileId(),form.getHeadCount(), form.getExpensePerHead(),
                            Constant.GIFTS_GIVING_TYPE);
            List<GiftsCopyToEntity> copyToList =
                    giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.HOSPITALITY_TYPE,
                            form.getCopyToUserEmails(),user);
            List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            log.info("copy to user emails: {}", copyToUserEmails);
            HospitalityRefEntity hospRef = updateHospRef(currentDate,applicationId,form);
            startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form);
        }
    }

    @Override
    @MasterTransactional
    public void saveGivingHospitality(GivingHospitalityFrom form) {
        log.info("save giving hospitality...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        HospitalityApplicationEntity application = saveHospitalityApplication(currentDate,user,form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info("applicationId: {}", applicationId);
        HospitalityActivityEntity activity = saveHospitalityActivity(currentDate,application,form);
        List<HospitalityRelationPersonEntity> hospPersonList =
                giftsCompanyService.saveOrUpdateHospPerson(form.getCompanyList(),currentDate,applicationId,
                        userId, form.getFileId(),form.getHeadCount(), form.getExpensePerHead(),
                        Constant.GIFTS_GIVING_TYPE);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.HOSPITALITY_TYPE,
                        form.getCopyToUserEmails(),user);
        List<String> copyToUserEmails = copyToList.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
        log.info("copy to user emails: {}", copyToUserEmails);
        HospitalityRefEntity hospRef = saveHospRef(currentDate,applicationId, form);
        startProcess(application,user,hospRef,hospPersonList,copyToUserEmails, form);
    }


    private void startProcess(HospitalityApplicationEntity application,
                              UserExtensionEntity user,
                              HospitalityRefEntity hospRef,
                              List<HospitalityRelationPersonEntity> hospPersonList,
                              List<String> copyToUserEmails, GivingHospitalityFrom from) {
        if(Constant.GIFT_SUBMIT_TYPE.equals(from.getActionType())){
            log.info("start giving hospitality process...");
        }
    }

    private HospitalityRefEntity updateHospRef(Date currentDate, Long applicationId, GivingHospitalityFrom form) {
        log.info("update giving hospitality reference...");
        HospitalityRefEntity giftsRef = hospitalityRefDao.selectOne(Wrappers.<HospitalityRefEntity>lambdaQuery()
                .eq(HospitalityRefEntity::getApplicationId,applicationId));
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

    private HospitalityRefEntity saveHospRef(Date currentDate, Long applicationId,GivingHospitalityFrom form) {
        log.info("save giving hospitality reference...");
        HospitalityRefEntity giftsRef = new HospitalityRefEntity();
        BeanUtils.copyProperties(form, giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setHospitalityDate(form.getDate());
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
        hospitalityRefDao.insert(giftsRef);
        return giftsRef;
    }


    private HospitalityActivityEntity saveHospitalityActivity(Date currentDate,HospitalityApplicationEntity application,
                                                              GivingHospitalityFrom form) {
        log.info("save giving hospitality activity...");
        HospitalityActivityEntity activity = new HospitalityActivityEntity();
        activity.setApplicationId(application.getApplicationId());
        activity.setSfUserIdSubmitter(application.getSfUserIdCreator());
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        hospitalityActivityDao.insert(activity);
        return activity;
    }

    private HospitalityActivityEntity updateHospitalityActivity(Date currentDate,GivingHospitalityFrom form) {
        log.info("update giving hospitality activity...");
        HospitalityActivityEntity activity = hospitalityActivityDao.selectOne(Wrappers.<HospitalityActivityEntity>lambdaQuery().
                eq(HospitalityActivityEntity::getApplicationId,form.getApplicationId()));
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


    private HospitalityApplicationEntity updateHospitalityApplication(Date currentDate,
                                                                      UserExtensionEntity user,
                                                                      GivingHospitalityFrom form) {
        log.info("update giving hospitality application...");
        HospitalityApplicationEntity app = hospitalityApplicationDao.selectById(form.getApplicationId());
        if(Objects.isNull(app)){
            log.info("empty giving hospitality application...");
            return null;
        }
        if(!Constant.GIFTS_DRAFT_TYPE.equals(app.getStatus())){
            log.info("giving gifts status: {} , can not be change", app.getStatus());
            return null;
        }
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setStatus(form.getActionType());
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setLastModifiedDate(currentDate);
        app.setEstimatedTotalExpense(form.getEstimatedTotalExpense());
        hospitalityApplicationDao.updateById(app);
        return app;

    }

    private HospitalityApplicationEntity saveHospitalityApplication(Date currentDate,
                                                              UserExtensionEntity user,
                                                              GivingHospitalityFrom form) {
        log.info("save hospitality application...");
        HospitalityApplicationEntity app = new HospitalityApplicationEntity();
        String reference = giftsDropDownService.getReference();
        BeanUtils.copyProperties(user,app);
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setSfUserIdCreator(user.getSfUserId());
        app.setSupervisorId(user.getSupervisorId());
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
        app.setIsUsed(Constant.EXIST_MARK);
        app.setNewVersion(Constant.EXIST_MARK);
        fillInDepartmentHead(app);
        hospitalityApplicationDao.insert(app);
        return app;
    }


    private void fillInDepartmentHead(HospitalityApplicationEntity app) {
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

    @Override
    @MasterTransactional
    public void cancelGivingHospitality(GivingHospitalityFrom form) {
        log.info("cancel giving hospitality...");
    }

    @Override
    @MasterTransactional
    public void deleteDraftGivingHospitality(Long applicationId) {
        log.info("delete giving hospitality...");
        HospitalityApplicationEntity app = hospitalityApplicationDao.selectById(applicationId);
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
        hospitalityRefDao.delete(Wrappers.<HospitalityRefEntity>lambdaQuery()
                .eq(HospitalityRefEntity::getApplicationId, applicationId));
        log.info("delete giving hospitality activity...");
        hospitalityActivityDao.delete(Wrappers.<HospitalityActivityEntity>lambdaQuery()
                .eq(HospitalityActivityEntity::getApplicationId, applicationId));
        log.info("delete giving hospitality relation person...");
        giftsCompanyService.deleteGiftsRelationPersonByApplicationId(applicationId,Constant.GIFTS_TYPE,Constant.GIFTS_GIVING_TYPE);
        giftsCopyToService.remove(Wrappers.<GiftsCopyToEntity>lambdaQuery()
                .eq(GiftsCopyToEntity::getApplicationId, applicationId)
                .eq(GiftsCopyToEntity::getType, Constant.GIFTS_GIVING_TYPE));
    }

    @Override
    public HospitalityApplicationEntity getGivingHospitalityByApplicationId(Long applicationId) {
        log.info("get giving hospitality...");
        log.info("get giving hospitality: {}",applicationId);
        HospitalityApplicationEntity application = hospitalityApplicationDao.selectById(applicationId);
        if(Objects.isNull(application)){
            return null;
        }
        UserExtensionEntity user = userInfoService.getById(application.getSfUserIdAppliedFor());
        if(Objects.nonNull(user)){
            application.setSfUserAppliedName(user.getFirstName() + " " + user.getLastName());
            application.setSfUserAppliedEmail(user.getEmail());
            application.setSfUserAppliedCwid(user.getCwid());
        }

        HospitalityRefEntity references = hospitalityRefDao.selectOne(Wrappers.<HospitalityRefEntity>lambdaQuery().
                eq(HospitalityRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,Constant.GIFTS_GIVING_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,Constant.HOSPITALITY_TYPE,
                Constant.GIFTS_GIVING_TYPE);
        log.info("Company size: {}", companyList.size());
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<HospitalityActivityEntity> hospActivities =
                hospitalityApplicationDao.queryGivingHospitalityActivityList(activityParam);
        log.info("giftsActivities size: {}", hospActivities.size());
        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,Constant.HOSPITALITY_TYPE,"CompanyPerson");
        if(Objects.nonNull(fileAttach)){
            log.info("gifts file attachment: {}", fileAttach.getFileName());
            application.setFileAttach(fileAttach);
        }
        application.setHospRef(references);
        application.setCopyToUsers(copyToUsers);
        application.setHospActivities(hospActivities);
        application.setCompanyList(companyList);
        return application;
    }

    @Override
    public Pagination<HospitalityApplicationEntity> getGivingHospitalityApplicationList(GiftsApplicationParam param) {
        log.info("page giving hospitality...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        IPage<HospitalityApplicationEntity> page = hospitalityApplicationDao.queryGivingHospitalityApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }
}
