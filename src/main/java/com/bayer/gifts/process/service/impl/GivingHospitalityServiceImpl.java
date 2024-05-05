package com.bayer.gifts.process.service.impl;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.GivingHospitalityActivityDao;
import com.bayer.gifts.process.dao.GivingHospitalityApplicationDao;
import com.bayer.gifts.process.dao.GivingHospitalityRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.form.GivingHospitalityFrom;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.*;
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
    GivingHospitalityApplicationDao givingHospitalityApplicationDao;
    @Autowired
    GivingHospitalityActivityDao givingHospitalityActivityDao;
    @Autowired
    GivingHospitalityRefDao givingHospitalityRefDao;

    @Override
    public void updateGivingHospitality(GivingHospitalityFrom form) {
        log.info("update giving hospitality...");
    }

    @Override
    public void saveGivingHospitality(GivingHospitalityFrom form) {
        log.info("save giving hospitality...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        HospitalityApplicationEntity application = saveHospitalityApplication(currentDate,user,form);
        Long applicationId = application.getApplicationId();
        Long userId = application.getSfUserIdAppliedFor();
        log.info("applicationId: {}", applicationId);
        HospitalityActivityEntity activity = saveHospitalityActivity(currentDate,application,form);
        List<GiftsCopyToEntity> copyToList =
                giftsCopyToService.saveOrUpdateGiftsCopyTo(applicationId,Constant.GIFTS_COPY_HOSPITALITY_TYPE,
                        form.getCopyToUserEmails(),user);
        List<Long> copyToUserIds = copyToList.stream().map(GiftsCopyToEntity::getSfUserIdCopyTo).collect(Collectors.toList());
        log.info("copy to user ids: {}", copyToUserIds);

    }

    private HospitalityActivityEntity saveHospitalityActivity(Date currentDate,HospitalityApplicationEntity application,
                                                              GivingHospitalityFrom form) {
        log.info("save giving gifts activity...");
        HospitalityActivityEntity activity = new HospitalityActivityEntity();
        activity.setApplicationId(application.getApplicationId());
        activity.setSfUserIdSubmitter(application.getSfUserIdCreator());
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        givingHospitalityActivityDao.insert(activity);
        return activity;
    }


    private HospitalityApplicationEntity saveHospitalityApplication(Date currentDate,
                                                              UserExtensionEntity user,
                                                              GivingHospitalityFrom form) {
        log.info("save gifts application...");
        HospitalityApplicationEntity app = new HospitalityApplicationEntity();
        String reference = giftsDropDownService.getReference();
        BeanUtils.copyProperties(user,app);
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setSfUserIdCreator(user.getSfUserId());
        app.setSupervisorId(user.getSupervisorId());
        app.setEmployeeLe(user.getCompanyCode());
        app.setDepartment(user.getOrgTxt());
//        app.setStatus(form.getActionType());
//        app.setReasonType(form.getReasonType());
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
        fillInDepartmentHead(app);

        givingHospitalityApplicationDao.insert(app);

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
    public void cancelGivingHospitality(GivingHospitalityFrom form) {
        log.info("cancel giving hospitality...");
    }

    @Override
    public void deleteDraftGivingHospitality(Long applicationId) {
        log.info("delete giving hospitality...");
    }

    @Override
    public HospitalityApplicationEntity getGivingHospitalityByApplicationId(Long applicationId) {
        log.info("get giving hospitality...");
        return null;
    }

    @Override
    public Pagination<HospitalityApplicationEntity> getGivingHospitalityApplicationList(GiftsApplicationParam param) {
        log.info("page giving hospitality...");
        return null;
    }
}
