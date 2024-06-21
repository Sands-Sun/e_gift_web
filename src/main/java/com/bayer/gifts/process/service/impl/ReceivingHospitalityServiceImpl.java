package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.dao.ReceivingHospitalityApplicationDao;
import com.bayer.gifts.process.dao.ReceivingHospitalityRefDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.GiftsBaseService;
import com.bayer.gifts.process.service.GiftsCompanyService;
import com.bayer.gifts.process.service.GiftsCopyToService;
import com.bayer.gifts.process.service.ReceivingHospitalityService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("receivingHospitalityService")
public class ReceivingHospitalityServiceImpl implements ReceivingHospitalityService {

    @Autowired
    GiftsBaseService giftsBaseService;

    @Autowired
    GiftsCompanyService giftsCompanyService;
    @Autowired
    GiftsCopyToService giftsCopyToService;
    @Autowired
    ReceivingHospitalityApplicationDao hospitalityApplicationDao;
    @Autowired
    ReceivingHospitalityRefDao hospitalityRefDao;


    @Override
    public ReceivingHospApplicationEntity getReceivingHospitalityHistoryByApplicationId(Long applicationId) {
        log.info("get receiving hospitality...");
        log.info("get receiving hospitality: {}",applicationId);
        ReceivingHospApplicationEntity app = hospitalityApplicationDao.selectById(applicationId);
        if(Objects.isNull(app)){
            return null;
        }
        giftsBaseService.fillInUserInfo(app);
        ReceivingHospRefEntity references = hospitalityRefDao.selectOne(Wrappers.<ReceivingHospRefEntity>lambdaQuery().
                eq(ReceivingHospRefEntity::getApplicationId,applicationId));
        List<GiftsCopyToEntity> copyToUsers = giftsCopyToService.getGiftsCopyToList(applicationId, Constant.HOSPITALITY_TYPE);
        log.info("copyToUser size: {}", copyToUsers.size());
//        List<GiftsCompanyEntity> companyList = giftsCompanyService.getHisComPersonByApplicationId(applicationId);
        GiftsActivityParam activityParam = GiftsActivityParam.builder().applicationId(applicationId).build();
        List<ReceivingHospActivityEntity> hospActivities =
                hospitalityApplicationDao.queryReceivingHospitalityActivityList(activityParam);
        log.info("giftsActivities size: {}", hospActivities.size());
        app.setHospRef(references);
        app.setCopyToUsers(copyToUsers);
        app.setHospActivities(hospActivities);
//        app.setCompanyList(companyList);
        return app;

    }

    @Override
    public Pagination<ReceivingHospApplicationEntity> getReceivingHospitalityApplicationList(GiftsApplicationParam param) {
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
        IPage<ReceivingHospApplicationEntity> page = hospitalityApplicationDao.queryReceivingHospitalityApplicationList(
                new Page<>(param.getCurrentPage(), param.getPageSize()),param);
        return new Pagination<>(page);
    }
}
