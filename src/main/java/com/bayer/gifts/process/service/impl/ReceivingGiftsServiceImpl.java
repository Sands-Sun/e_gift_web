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
import com.bayer.gifts.process.entity.GivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.entity.GivingGiftsRefEntity;
import com.bayer.gifts.process.entity.ReceivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.ReceivingGiftsApplicationEntity;
import com.bayer.gifts.process.entity.ReceivingGiftsRefEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.form.GiftsFormBase;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.form.ReceivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.GiftsDropDownService;
import com.bayer.gifts.process.service.ReceivingGiftsService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service("receivingGiftsService")
public class ReceivingGiftsServiceImpl implements ReceivingGiftsService {

    @Autowired
    GiftsDropDownService giftsDropDownService;

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
        saveGiftsRef(currentDate,applicationId,form);
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
            updateGiftsActivity(currentDate,form);
            updateGiftsRef(currentDate,applicationId,form);
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
        app.setIsHandedOver(form.getIsHandedOver());
        app.setIsExcluded(form.getIsExcluded());
        app.setIsInvolved(form.getIsInvolved());

        app.setStatus(form.getActionType());
        app.setStatus(Constant.GIFTS_DRAFT_TYPE);
        app.setReason(form.getReason());
        app.setRemark(form.getRemark());
        app.setReference(reference);
        app.setEstimatedTotalValue(form.getUnitValue() * form.getVolume());
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
        activity.setAction(form.getActionType());
        activity.setRemark(form.getRemark());
        activity.setCreatedDate(currentDate);
        activity.setLastModifiedDate(currentDate);
        receivingGiftsActivityDao.insert(activity);
    }


    private void saveGiftsRef(Date currentDate, Long applicationId,ReceivingGiftsForm form) {
        log.info("save receiving gifts reference...");
        ReceivingGiftsRefEntity giftsRef = new ReceivingGiftsRefEntity();
        BeanUtils.copyProperties(form,giftsRef);
        giftsRef.setApplicationId(applicationId);
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setCreatedDate(currentDate);
        giftsRef.setLastModifiedDate(currentDate);
        receivingGiftsRefDao.insert(giftsRef);
    }


    private void updateGiftsRef(Date currentDate, Long applicationId,ReceivingGiftsForm form) {
        log.info("update receiving gifts reference...");
        ReceivingGiftsRefEntity giftsRef = receivingGiftsRefDao.selectOne(Wrappers.<ReceivingGiftsRefEntity>lambdaQuery()
                .eq(ReceivingGiftsRefEntity::getApplicationId,applicationId));
        if(Objects.isNull(giftsRef)){
            return;
        }
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setGiftDesc(form.getGiftDesc());
        giftsRef.setGiftDescType(form.getGiftDescType());
        giftsRef.setUnitValue(form.getUnitValue());
        giftsRef.setVolume(form.getVolume());
        giftsRef.setGivingDate(form.getDate());
        giftsRef.setIsSco(form.getIsSco());
        giftsRef.setGivingPerson(form.getGivingPerson());
        giftsRef.setGivingCompany(form.getGivingCompany());
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
