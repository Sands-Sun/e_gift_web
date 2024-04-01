package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.param.GiftsGroupParam;

import java.util.List;
public interface GiftsGroupService extends IService<GiftsGroupEntity> {

    void saveGiftsGroup(GiftsGroupParam param);

    void updateGiftsGroup(GiftsGroupParam param);

    void deleteGiftsGroup(Long id);

    void saveOrUpdateUserToGroup(GiftsGroupEntity giftsGroup, GiftsGroupParam param);

    GiftsGroupEntity getGiftsGroupById(Long id);

    GiftsGroupEntity getGiftsGroupByCode(String code);

    Pagination<GiftsGroupEntity> getGiftsGroupList(GiftsGroupParam param);

    List<GiftsGroupEntity> getGroupListByUserId(Long userId);

    List<GiftsGroupEntity> getAllGroupList();
}
