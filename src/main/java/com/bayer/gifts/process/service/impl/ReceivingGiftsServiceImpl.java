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
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.form.ReceivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.GiftsCompanyService;
import com.bayer.gifts.process.service.GiftsCopyToService;
import com.bayer.gifts.process.service.GiftsDropDownService;
import com.bayer.gifts.process.service.ReceivingGiftsService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("receivingGiftsService")
public class ReceivingGiftsServiceImpl implements ReceivingGiftsService {

    @Autowired
    GiftsDropDownService giftsDropDownService;

    @Autowired
    GiftsCompanyService giftsCompanyService;

    @Autowired
    GiftsCopyToService giftsCopyToService;

    @Autowired
    ReceivingGiftsRefDao receivingGiftsRefDao;

    @Autowired
    ReceivingGiftsActivityDao receivingGiftsActivityDao;

    @Autowired
    ReceivingGiftsApplicationDao receivingGiftsApplicationDao;




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
        Double unitValue = Objects.nonNull(form.getEstimatedTotalValue()) ? form.getEstimatedTotalValue() : form.getUnitValue();
        List<GiftsRelationPersonEntity> giftsPersonList =
                giftsCompanyService.saveOrUpdateGiftsPerson(currentDate,applicationId,userId,
                        form.getGivingTitle(),form.getGivingCompany(),form.getGivingPersons(), unitValue);
        saveGiftsRef(currentDate,applicationId,giftsPersonList,form);
    }

    @Override
    @MasterTransactional
    public void updateDraftReceivingGifts(ReceivingGiftsForm form) {
        log.info("update receiving gifts...");
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        ReceivingGiftsApplicationEntity application = updateGiftsApplication(currentDate,user,form);
        if(Objects.nonNull(application)){
            Long applicationId = application.getApplicationId();
            log.info("applicationId: {}", applicationId);
            saveGiftsActivity(currentDate,application,form);
            Long userId = application.getSfUserIdAppliedFor();
            List<GiftsRelationPersonEntity> giftsPersonList =
                    giftsCompanyService.saveOrUpdateGiftsPerson(currentDate,applicationId,userId,
                            form.getGivingTitle(),form.getGivingCompany(),form.getGivingPersons(), form.getUnitValue());
            updateGiftsRef(currentDate,applicationId,giftsPersonList,form);
        }
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
        app.setSfUserIdAppliedFor(Objects.isNull(form.getApplyForId()) ? user.getSfUserId() : form.getApplyForId());
        app.setStatus(form.getActionType());
        app.setStatus(Constant.GIFTS_DRAFT_TYPE);
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
        app.setIsHandedOver(Constant.EXIST_MARK.equals(form.getIsHandedOver()) ? "Y" : "N");
        app.setIsExcluded(form.getIsExcluded());
        app.setIsInvolved(form.getIsInvolved());

        app.setStatus(form.getActionType());
        app.setStatus(Constant.GIFTS_DRAFT_TYPE);
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setReference(reference);
        if(Objects.nonNull(form.getEstimatedTotalValue())){
            app.setEstimatedTotalValue(form.getEstimatedTotalValue());
        }else {
            app.setEstimatedTotalValue(form.getUnitValue() * form.getVolume());
        }
        app.setCreatedDate(currentDate);
        app.setLastModifiedDate(currentDate);
        app.setMarkDeleted(Constant.NO_EXIST_MARK);
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


    private void saveGiftsRef(Date currentDate, Long applicationId,
                              List<GiftsRelationPersonEntity> giftsPersonList,
                              ReceivingGiftsForm form) {
        log.info("save receiving gifts reference...");
        String givingPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givingCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName).
                findFirst().orElse(StringUtils.EMPTY);
        log.info("give persons: {}", givingPersons);
        log.info("give company: {}", givingCompany);
        String isHandeOver = form.getIsHandedOver();
        String isSco;
        if(Constant.EXIST_MARK.equals(isHandeOver)) {
            isSco = Constant.YES_MARK;
        }else if(Constant.NO_EXIST_MARK.equals(isHandeOver)){
            isSco = Constant.NO_MARK;
        }else {
            isSco = Constant.GIFTS_NOT_APPLICABLE;
        }
        ReceivingGiftsRefEntity giftsRef = new ReceivingGiftsRefEntity();
        BeanUtils.copyProperties(form,giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setGivingCompany(givingCompany);
        giftsRef.setGivingPerson(givingPersons);
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setIsSco(isSco);
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
        receivingGiftsRefDao.insert(giftsRef);
    }


    private void updateGiftsRef(Date currentDate, Long applicationId,
                                List<GiftsRelationPersonEntity> giftsPersonList,ReceivingGiftsForm form) {
        log.info("update receiving gifts reference...");
        ReceivingGiftsRefEntity giftsRef = receivingGiftsRefDao.selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery()
                .eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return;
        }
        String givingPersons = giftsPersonList.stream().
                map(GiftsRelationPersonEntity::getPersonName).collect(Collectors.joining(","));
        String givingCompany = giftsPersonList.stream().map(GiftsRelationPersonEntity::getCompanyName).
                findFirst().orElse(StringUtils.EMPTY);
        log.info("give persons: {}", givingPersons);
        log.info("give company: {}", givingCompany);

        String isSco = Constant.EXIST_MARK.equals(form.getIsHandedOver()) ? "Yes" : "No";
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setGiftDesc(form.getGiftDesc());
        giftsRef.setGiftDescType(form.getGiftDescType());
        giftsRef.setUnitValue(form.getUnitValue());
        giftsRef.setVolume(form.getVolume());
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setIsSco(isSco);
        giftsRef.setGivingPerson(givingPersons);
        giftsRef.setGivingCompany(givingCompany);
        giftsRef.setLastModifiedDate(currentDate);
        receivingGiftsRefDao.updateById(giftsRef);
    }


    @Override
    public ReceivingGiftsApplicationEntity getReceivingGiftsByApplicationId(Long applicationId) {
        log.info("get receiving gifts: {}",applicationId);
        ReceivingGiftsApplicationEntity application = receivingGiftsApplicationDao.selectById(applicationId);
        if(Objects.isNull(application)){
            return null;
        }
        ReceivingGiftsRefEntity references = receivingGiftsRefDao.
                selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery().
                        eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        application.setGiftsRef(references);
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
}
