package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.dao.*;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.FormBase;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.GiftsBaseNoticeMailVo;
import com.bayer.gifts.process.mail.vo.GivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.mail.vo.GivingHospProcessNoticeMailVo;
import com.bayer.gifts.process.mail.vo.ReceivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.service.*;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    GivingGiftsRefDao givingGiftsRefDao;

    @Autowired
    ReceivingGiftsRefDao receivingGiftsRefDao;

    @Autowired
    GivingHospitalityRefDao givingHospitalityRefDao;
    @Autowired
    BatchCompleteMailService completeMailService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    StorageService storageService;
    @Autowired
    GiftsCopyToService giftsCopyToService;

    @Autowired
    GiftsCompanyService giftsCompanyService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    GiftsDropDownService giftsDropDownService;

    @Override
    public String getProcessInstanceKey(UserExtensionEntity user,String processTypePrefix) {
        String processInstanceKey;
        String companyCode = user.getCompanyCode();
        if(user.checkIsCountryHead()){
            log.info("process for country head...");
            processInstanceKey = getProcessInstanceKey(companyCode, processTypePrefix + Constant.COUNTRY_HEAD_PROCESS_TYPE_PREFIX);
        } else if(user.checkIsDepartmentHead()) {
            log.info("process for department head...");
            processInstanceKey = getProcessInstanceKey(companyCode, processTypePrefix + Constant.DEPT_HEAD_PROCESS_TYPE_PREFIX);
        }else {
            log.info("process for normal...");
            processInstanceKey = getProcessInstanceKey(companyCode, processTypePrefix);
        }
        return processInstanceKey;
    }

    private String getProcessInstanceKey(String companyCode, String processTypePrefix) {
        String processInstanceKey;
        if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
            processInstanceKey = processTypePrefix + companyCode;
        }else if(Constant.GIFTS_LE_CODE_SEM_2614.equals(companyCode) ||
                Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
            processInstanceKey = processTypePrefix + "2614_1391";
        }else {
            processInstanceKey = processTypePrefix + "0882_1954_1955";
        }
        return  processInstanceKey;
    }
    @Override
    public void fillInApplyForUser(UserExtensionEntity user, GiftsApplicationBaseEntity app) {
        if(Objects.nonNull(user)){
            app.setSfUserIdAppliedFor(user.getSfUserId());
            app.setSfUserAppliedEmail(user.getEmail());
            app.setSfUserAppliedCwid(user.getCwid());
            app.setSfUserAppliedName(user.getFirstName() + " " + user.getLastName());
            app.setSfUserAppliedFirstName(user.getFirstName());
            app.setSfUserAppliedLastName(user.getLastName());
            app.setSupervisorId(user.getSupervisorId());
            app.setApplyForUser(user);
        }
    }

    @Override
    public void fillInUserInfo(GiftsApplicationBaseEntity app) {
        Long applyUserId = app.getSfUserIdAppliedFor();
        Long creatorUserId = app.getSfUserIdCreator();
        UserExtensionEntity applyForUser = new UserExtensionEntity();
        UserExtensionEntity user = userInfoService.getUserInfo(creatorUserId, false,false,true);
        app.setCreatorUser(user);
        if(Objects.equals(applyUserId, creatorUserId)){
            BeanUtils.copyProperties(user,applyForUser);
        }else {
            applyForUser = userInfoService.getUserInfo(applyUserId,false,false,true);
        }
        app.setApplyForUser(applyForUser);
    }
    @Override
    public void fillInCountryHead(GiftsApplicationProcessEntity app) {
        String companyCode = app.getEmployeeLe();
        String bizGroup = GiftsApplyBaseVariable.getBizGroupByCompanyCode(companyCode);
        GiftsGroupEntity countryHeadGroup = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_" + Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD);
        if(Objects.nonNull(countryHeadGroup) && CollectionUtils.isNotEmpty(countryHeadGroup.getUserToGroups())){
            GiftsUserToGroupEntity countryUser = countryHeadGroup.getUserToGroups().get(0);
            GiftsGroupEntity currentCountryHeadGroup = new GiftsGroupEntity();
            BeanUtils.copyProperties(countryHeadGroup, currentCountryHeadGroup);
            currentCountryHeadGroup.setUserToGroups(Collections.singletonList(countryUser));
            app.setDepartmentHeadId(countryUser.getUserId());
            app.setDepartmentHeadName(countryUser.getUserFirstName() + " " + countryUser.getUserLastName());
            log.info("country head id: {}",app.getDepartmentHeadId());
            log.info("country head name: {}", app.getDepartmentHeadName());
            app.setCountryHeadGroup(currentCountryHeadGroup);
        }

    }

    @Override
    public void fillInDepartmentHead(GiftsApplicationProcessEntity app) {
        if(Objects.nonNull(app.getDepartmentHeadId())) {
            log.info("add department head information...");
            GiftsUserToGroupEntity  deptHeadUser =
                    Constant.GIFTS_GROUP_MAP.entrySet().stream()
                            .flatMap(g -> g.getValue().getUserToGroups().stream())
                            .filter(g -> g.getUserId().equals(app.getDepartmentHeadId())).findFirst().orElse(null);
            if(Objects.nonNull(deptHeadUser)){
                log.info("found dept head from exist data..");
                GiftsGroupEntity deptHeadGroup = new GiftsGroupEntity();
                deptHeadGroup.setUserToGroups(Collections.singletonList(deptHeadUser));
                app.setDeptHeadGroup(deptHeadGroup);
            }else {
                log.info("not found dept head from exist data..");
                fillInDeptHead(app);
            }
        }
    }

    private void fillInDeptHead(GiftsApplicationProcessEntity app) {
        UserExtensionEntity deptHeadUser = userInfoService.getById(app.getDepartmentHeadId());
        if(Objects.nonNull(deptHeadUser)){
            GiftsGroupEntity deptHeadGroup = new GiftsGroupEntity();
            GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
            BeanUtils.copyProperties(deptHeadGroup, userToGroup);
            userToGroup.setUserId(deptHeadUser.getSfUserId());
            userToGroup.setUserEmail(deptHeadUser.getEmail());
            userToGroup.setUserCwid(deptHeadUser.getCwid());
            userToGroup.setUserFirstName(deptHeadUser.getFirstName());
            userToGroup.setUserLastName(deptHeadUser.getLastName());
            deptHeadGroup.setUserToGroups(Collections.singletonList(userToGroup));
            app.setDeptHeadGroup(deptHeadGroup);
        }
    }


    @Override
    public void fillInDepartmentHead(GiftsApplicationProcessEntity app, FormBase form, String division) {
        log.info("fillIn department head information...");
        if(StringUtils.isEmpty(form.getDeptHead())){
            log.info("empty department head from request...");
            return;
        }
        String deptHeadEmail = form.getDeptHead();
        String companyCode = app.getEmployeeLe();
        String bizGroup = GiftsApplyBaseVariable.getBizGroupByCompanyCode(companyCode);//DEPARTMENT_HEAD
        String divisionPrefix = Constant.GIFTS_BIZ_GROUP_BHC_NAME.equals(bizGroup) ? division : StringUtils.EMPTY;
        GiftsGroupEntity departmentHeadGroup =
                Constant.GIFTS_GROUP_MAP.get(bizGroup + divisionPrefix+ "_" + Constant.GIFTS_LEADERSHIP_DEPARTMENT_HEAD);
        if(Objects.nonNull(departmentHeadGroup) && CollectionUtils.isNotEmpty(departmentHeadGroup.getUserToGroups())){
            GiftsUserToGroupEntity departmentUser = departmentHeadGroup.getUserToGroups()
                    .stream().filter(d -> deptHeadEmail.equals(d.getUserEmail())).findFirst().orElse(null);
            if(Objects.isNull(departmentUser)){
                log.info("not match department from department groups...");
                return;
            }
            GiftsGroupEntity currentDeptHeadGroup = new GiftsGroupEntity();
            BeanUtils.copyProperties(departmentHeadGroup, currentDeptHeadGroup);
            currentDeptHeadGroup.setUserToGroups(Collections.singletonList(departmentUser));
            app.setDepartmentHeadId(departmentUser.getUserId());
            app.setDepartmentHeadName(departmentUser.getUserFirstName() + " " + departmentUser.getUserLastName());
            log.info("department head id: {}",app.getDepartmentHeadId());
            log.info("department head name: {}", app.getDepartmentHeadName());
            app.setDeptHeadGroup(currentDeptHeadGroup);
        }
    }

    @Override
    @MasterTransactional
    public Long copyGiftsRecord(Long applicationId,String type) {
        GiftsApplicationBaseEntity app = getGiftsApplicationById(applicationId,type);
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        if(Objects.isNull(app)){
            log.info("empty gifts application...");
            return null;
        }
        Date currentDate = new Date();
        GiftsApplicationBaseEntity copyApp = copyApplication(type,currentDate,app);
        Long copyApplicationId = copyApp.getApplicationId();
        GiftsRefBaseEntity ref = getGiftsRefByApplicationId(applicationId,type,currentDate);
        copyRef(type,currentDate,copyApplicationId,ref);
        saveGiftsActivity(currentDate,type,Constant.GIFTS_DRAFT_TYPE,copyApp.getRemark(),copyApp);
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId,type);
        log.info("copyToUser size: {}", copyToUsers.size());
        if(CollectionUtils.isNotEmpty(copyToUsers)){
            List<String> copyEmails = copyToUsers.stream().map(GiftsCopyToEntity::getCopytoEmail).collect(Collectors.toList());
            giftsCopyToService.saveOrUpdateGiftsCopyTo(copyApplicationId, type, copyEmails,user);
        }
//        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,Constant.HOSPITALITY_TYPE,
//                Constant.GIFTS_GIVING_TYPE);
        List<GiftsCompanyEntity> companyList = giftsCompanyService.getCompPersonByApplicationId(applicationId,
                Constant.HOSPITALITY_TYPE.equals(type) ? type : Constant.GIFTS_TYPE,
                Constant.HOSPITALITY_TYPE.equals(type) ? Constant.GIFTS_GIVING_TYPE : type);
        log.info("Company size: {}", companyList.size());
        if(CollectionUtils.isNotEmpty(companyList)){
            List<GiftsPersonEntity> personList = companyList.stream()
                    .flatMap(c -> c.getPersonList().stream()).collect(Collectors.toList());
            saveOrUpdateGiftsPerson(currentDate,type,copyApplicationId,ref,personList);
        }
        FileUploadEntity fileAttach = storageService.getUploadFile(applicationId,type,Constant.COMPANY_PERSON_ATTACH_MODULE);
        if(Objects.nonNull(fileAttach)){
            FileUploadEntity copyFileAttach = storageService.copyDownloadFile(fileAttach);
            FileMapEntity fileMap = new FileMapEntity();
            fileMap.setApplicationId(copyApplicationId);
            fileMap.setFileId(copyFileAttach.getId());
            fileMap.setModule(type);
            fileMap.setType(Constant.COMPANY_PERSON_ATTACH_MODULE);
            fileMap.setCreatedBy(String.valueOf(user.getSfUserId()));
            fileMap.setCreatedDate(currentDate);
            fileMap.setLastModifiedBy(String.valueOf(user.getSfUserId()));
            fileMap.setLastModifiedDate(currentDate);
            storageService.saveFileMap(fileMap);
        }
        return copyApplicationId;
    }


    @Override
    public void copyToGiftsProcess(GiftsApplyBaseVariable variable, String type) {
        log.info("copy to gifts...");
        if(CollectionUtils.isEmpty(variable.getCopyToUserEmails())){
            log.info("empty copy to user emails...");
            return;
        }
        GiftsApplyBaseVariable ccBaseVariable;
        GiftsBaseNoticeMailVo noticeMailVo = null;
        if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            ccBaseVariable = new ReceivingGiftsApplyVariable();
            BeanUtils.copyProperties(variable,ccBaseVariable);
            ccBaseVariable.setActionType(Constant.GIFTS_COPY_TYPE);
            noticeMailVo = new ReceivingGiftsProcessNoticeMailVo(ccBaseVariable);
        }else if(Constant.HOSPITALITY_TYPE.equals(type)) {
            ccBaseVariable = new GivingHospApplyVariable();
            BeanUtils.copyProperties(variable,ccBaseVariable);
            ccBaseVariable.setActionType(Constant.GIFTS_COPY_TYPE);
            noticeMailVo = new GivingHospProcessNoticeMailVo(ccBaseVariable);
        }else {
            ccBaseVariable = new GivingGiftsApplyVariable();
            BeanUtils.copyProperties(variable,ccBaseVariable);
            ccBaseVariable.setActionType(Constant.GIFTS_COPY_TYPE);
            noticeMailVo = new GivingGiftsProcessNoticeMailVo(ccBaseVariable);
        }
        processSendMail(noticeMailVo);
    }

    @Override
//    @Async("threadExecutor")
    public void documentGiftsProcess(ActivitiEvent event) {
        log.info("document gifts...");
        String processId = event.getProcessInstanceId();
        String definitionId =  event.getProcessDefinitionId();
        Map<String, Object> runtimeVar = runtimeService.getVariables(processId);
        log.info("runtimeVar >>>>> {}", runtimeVar);
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
    @Async("threadExecutor")
    public void cancelGiftsProcess(FormBase form, String type) {
        log.info("cancel gifts...");
        Long applicationId = form.getApplicationId();
        GiftsApplicationBaseEntity app = getGiftsApplicationById(applicationId,type);
        if(Objects.isNull(app)){
            log.info("empty gifts application...");
            return;
        }
        String  processInsId = String.valueOf(((GiftsApplicationProcessEntity) app).getSfProcessInsId());
        log.info("gifts processInsId: {}", processInsId);
        Map<String, Object> runtimeVar  = runtimeService.getVariables(processInsId);
        GiftsActivityBaseEntity activity = getGiftsActivityBaseLastOne(applicationId,type);
        if(updateAndProcessBusiness(app,activity,runtimeVar,Constant.GIFTS_CANCELLED_TYPE,type,
                Constant.GIFTS_CANCELLED_TYPE, Constant.GIFTS_CANCELLED_TYPE,true)){
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
            String signUserName;
            if(a.getSfUserIdSubmitter() == 9999){
                signUserName = "System";
            }else {
                signUserName = a.getUserFirstName() + StringUtils.SPACE + a.getUserLastName();
            }
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
        List<? extends GiftsActivityBaseEntity> activityList = getGiftsActivityBaseByType(app.getApplicationId(), type);
        if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            variable = (ReceivingGiftsApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_RECEIVING_GIFTS_VARIABLE);
            variable.setActionType(actionType);
            variable.setActivityList(activityList);
            noticeMailVo = new ReceivingGiftsProcessNoticeMailVo(variable);
        }else if(Constant.HOSPITALITY_TYPE.equals(type)) {
            variable = (GivingHospApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE);
            variable.setActionType(actionType);
            variable.setActivityList(activityList);
            noticeMailVo = new GivingHospProcessNoticeMailVo(variable);
        }else {
            variable = (GivingGiftsApplyVariable) runtimeVar.get(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE);
            variable.setActionType(actionType);
            variable.setActivityList(activityList);
            noticeMailVo = new GivingGiftsProcessNoticeMailVo(variable);
        }

        if(needUpt) {
            app.setStatus(status);
            updateApplicationById(type,app);
            saveGiftsActivity(currentDate,type,status,remark,app);
        }
        setSignatureAndRemark(variable,type);
        noticeMailVo.resetSignatureAndRemark(variable);
        processSendMail(noticeMailVo);
        return true;
    }



    private void processSendMail(GiftsBaseNoticeMailVo noticeMailVo) {
        completeMailService.completeAndSentMail(noticeMailVo);
    }


    private GiftsApplicationProcessEntity getGiftsApplicationByProcId(Long processId, String type) {
        GiftsApplicationProcessEntity app;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            app = hospitalityApplicationDao.selectOne(
                    Wrappers.<GivingHospApplicationEntity>lambdaQuery()
                            .eq(GivingHospApplicationEntity::getSfProcessInsId, processId));
        } else {
            app = givingGiftsApplicationDao.selectOne(
                    Wrappers.<GivingGiftsApplicationEntity>lambdaQuery()
                            .eq(GivingGiftsApplicationEntity::getSfProcessInsId, processId));
        }
        return app;
    }
    private GiftsApplicationBaseEntity getGiftsApplicationById(Long applicationId,String type) {
        GiftsApplicationBaseEntity app = null;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            app = hospitalityApplicationDao.selectById(applicationId);
        } else if(Constant.GIFTS_GIVING_TYPE.equals(type)){
            app = givingGiftsApplicationDao.selectById(applicationId);
        }else if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            app = receivingGiftsApplicationDao.selectById(applicationId);
        }
        return app;
    }

    private GiftsRefBaseEntity getGiftsRefByApplicationId(Long applicationId,String type,Date currentDate) {
        GiftsRefBaseEntity refBaseEntity = null;
        if (Constant.HOSPITALITY_TYPE.equals(type)) {
            refBaseEntity = givingHospitalityRefDao.selectOne(Wrappers.<GivingHospRefEntity>lambdaQuery().
                    eq(GivingHospRefEntity::getApplicationId,applicationId));
        } else if(Constant.GIFTS_GIVING_TYPE.equals(type)){
            refBaseEntity = givingGiftsRefDao.selectOne(Wrappers.<GivingGiftsRefEntity>lambdaQuery().
                    eq(GivingGiftsRefEntity::getApplicationId,applicationId));


        }else if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            refBaseEntity = receivingGiftsRefDao.selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery().
                    eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        }
        return refBaseEntity;
    }

    @Override
    public GiftsActivityBaseEntity getGiftsActivityBaseLastOne(Long applicationId, String type) {
        GiftsActivityBaseEntity activity = null;
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
            GivingHospActivityEntity activity = fillInActivityEntity(currentDate,status,remark,app, GivingHospActivityEntity.class);
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
                hospitalityApplicationDao.updateById((GivingHospApplicationEntity) app);
                break;
            default:
                givingGiftsApplicationDao.updateById((GivingGiftsApplicationEntity) app);
        }
    }

    private void saveApplication(String type,GiftsApplicationBaseEntity app) {
        log.info("update application type: {}, id: {}", type, app.getApplicationId());
        switch (type){
            case Constant.GIFTS_RECEIVING_TYPE:
                ReceivingGiftsApplicationEntity receivingGiftApp = (ReceivingGiftsApplicationEntity)app;
                receivingGiftApp.setGivingDate(new Date());
                receivingGiftsApplicationDao.insert(receivingGiftApp);
                break;
            case Constant.HOSPITALITY_TYPE:
                GivingHospApplicationEntity hospitalApp = (GivingHospApplicationEntity) app;
                hospitalApp.setHospitalityDate(new Date());
                hospitalApp.setIsUsed(Constant.NO_EXIST_MARK);
                hospitalityApplicationDao.insert(hospitalApp);
                break;
            default:
                GivingGiftsApplicationEntity givingGiftApp = (GivingGiftsApplicationEntity) app;
                givingGiftApp.setGivenDate(new Date());
                givingGiftApp.setIsUsed(Constant.NO_EXIST_MARK);
                givingGiftApp.setConcurStatus(null);
                givingGiftApp.setConcurReportId(null);
                givingGiftsApplicationDao.insert(givingGiftApp);
        }
    }

    private void saveRef(String type,GiftsRefBaseEntity ref) {
        log.info("update application type: {}, id: {}", type, ref.getApplicationId());
        switch (type){
            case Constant.GIFTS_RECEIVING_TYPE:
                receivingGiftsRefDao.insert((ReceivingGiftsRefEntity)ref);
                break;
            case Constant.HOSPITALITY_TYPE:
                givingHospitalityRefDao.insert((GivingHospRefEntity) ref);
                break;
            default:
                givingGiftsRefDao.insert((GivingGiftsRefEntity) ref);
        }
    }

    private void saveOrUpdateGiftsPerson(Date currentDate,String type,Long copyApplicationId,
                                         GiftsRefBaseEntity ref,List<GiftsPersonEntity> personList) {
        if(Constant.HOSPITALITY_TYPE.equals(type)){
            GivingHospRefEntity hospRef = (GivingHospRefEntity) ref;
            giftsCompanyService.saveOrUpdateHospPerson(copyApplicationId,currentDate,
                    Constant.GIFTS_GIVING_TYPE,hospRef.getExpensePerHead(),personList);
        }else {
            giftsCompanyService.saveOrUpdateGiftsPerson(copyApplicationId,currentDate,type,personList);
        }
    }

    private GiftsApplicationBaseEntity copyApplication(String type,Date currentDate,GiftsApplicationBaseEntity app) {
        GiftsApplicationBaseEntity copyApp;
        String reference = giftsDropDownService.getReference();
        if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            copyApp = new ReceivingGiftsApplicationEntity();
        }else if(Constant.HOSPITALITY_TYPE.equals(type)){
            copyApp = new GivingHospApplicationEntity();
        }else {
            copyApp = new GivingGiftsApplicationEntity();
        }
        BeanUtils.copyProperties(app,copyApp,"useCase");
        copyApp.setStatus(Constant.GIFTS_DRAFT_TYPE);
        copyApp.setReference(reference);
        copyApp.setCreatedDate(currentDate);
        copyApp.setLastModifiedDate(currentDate);
        saveApplication(type,copyApp);
        return copyApp;
    }


    private void copyRef(String type,Date currentDate,Long copyApplicationId,GiftsRefBaseEntity ref) {
        GiftsRefBaseEntity copyRef;
        if(Constant.GIFTS_RECEIVING_TYPE.equals(type)){
            copyRef = new ReceivingGiftsRefEntity();
        }else if(Constant.HOSPITALITY_TYPE.equals(type)){
            copyRef = new GivingHospRefEntity();
        }else {
            copyRef = new GivingGiftsRefEntity();
        }
        BeanUtils.copyProperties(ref,copyRef);
        copyRef.setApplicationId(copyApplicationId);
        copyRef.setCreatedDate(currentDate);
        copyRef.setLastModifiedDate(currentDate);
        saveRef(type,copyRef);
    }




    private <T extends GiftsActivityBaseEntity> T fillInActivityEntity(Date currentDate,String status,String remark,
                                                                          GiftsApplicationBaseEntity app, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            t.setApplicationId(app.getApplicationId());
            t.setSfUserIdSubmitter(Constant.GIFTS_DOCUMENTED_TYPE.equals(remark) ? 9999L : app.getSfUserIdCreator());
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
