package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.GiftsRoleDao;
import com.bayer.gifts.process.entity.GiftsRoleEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.GiftsRoleParam;
import com.bayer.gifts.process.service.GiftsRoleService;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service("giftsRoleService")
public class GiftsRoleServiceImpl implements GiftsRoleService {


    @Autowired
    UserInfoService userInfoService;

    @Autowired
    GiftsRoleDao giftsRoleDao;

    @Override
    @MasterTransactional
    public void saveGiftsRole(GiftsRoleParam param) {
        log.info("save gifts role...");
        int count = giftsRoleDao.selectCount(Wrappers.<GiftsRoleEntity>lambdaQuery()
                .eq(GiftsRoleEntity::getRoleCode,param.getRoleCode()));
        if(count > 0 ){
            log.info("exist gifts role no need save...");
            return;
        }
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GiftsRoleEntity giftsRole = new GiftsRoleEntity();
        BeanUtils.copyProperties(param,giftsRole);
        giftsRole.setMarkDeleted(Constant.NO_EXIST_MARK);
        giftsRole.setCreatedBy(user.getSfUserId());
        giftsRole.setCreatedDate(currentDate);
        giftsRole.setLastModifiedBy(user.getSfUserId());
        giftsRole.setLastModifiedDate(currentDate);
        this.giftsRoleDao.insert(giftsRole);
        throw new RuntimeException("error");
    }

    @Override
    @MasterTransactional
    public void updateGiftsRole(GiftsRoleParam param) {
        log.info("update gifts role...");
        int count = giftsRoleDao.selectCount(Wrappers.<GiftsRoleEntity>lambdaQuery()
                .eq(GiftsRoleEntity::getId,param.getId()));
        if(count == 0 ){
            log.info("not found gifts role no need update...");
            return;
        }

        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GiftsRoleEntity giftsRole = new GiftsRoleEntity();
        BeanUtils.copyProperties(param,giftsRole);
        giftsRole.setLastModifiedBy(user.getSfUserId());
        giftsRole.setLastModifiedDate(currentDate);
        this.giftsRoleDao.updateById(giftsRole);
    }

    @Override
    public GiftsRoleEntity getGiftsRoleById(Long id) {
        return this.giftsRoleDao.selectOne(Wrappers.<GiftsRoleEntity>lambdaQuery().eq(GiftsRoleEntity::getId,id));
    }

    @Override
    public List<GiftsRoleEntity> getRoleListByUserId(Long userId) {
        return giftsRoleDao.queryRoleListByUserId(userId);
    }

    @Override
    @MasterTransactional
    public void deleteGiftsRole(Long id) {
        log.info("delete gifts role...");
        this.giftsRoleDao.update(null,Wrappers.<GiftsRoleEntity>lambdaUpdate()
                .set(GiftsRoleEntity::getMarkDeleted,Constant.EXIST_MARK)
                .eq(GiftsRoleEntity::getId,id));

    }

    @Override
    public Pagination<GiftsRoleEntity> getGiftsRoleList(GiftsRoleParam param) {
        log.info("get gifts role page...");
        IPage<GiftsRoleEntity> page = giftsRoleDao.queryGiftsRoleList(
                new Page<>(param.getCurrentPage(), param.getPageSize()), param);
        return new Pagination<>(page);
    }
}
