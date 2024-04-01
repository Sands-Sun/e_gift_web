package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.GiftsRoleEntity;
import com.bayer.gifts.process.param.GiftsRoleParam;

import java.util.List;

public interface GiftsRoleService  {


    void saveGiftsRole(GiftsRoleParam param);

    void updateGiftsRole(GiftsRoleParam param);

    void deleteGiftsRole(Long id);

    GiftsRoleEntity getGiftsRoleById(Long id);

    Pagination<GiftsRoleEntity> getGiftsRoleList(GiftsRoleParam param);

    List<GiftsRoleEntity> getRoleListByUserId(Long userId);
}
