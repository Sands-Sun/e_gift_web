package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserParam;
import com.bayer.gifts.process.param.UserSearchParam;

import java.util.List;

public interface UserInfoService extends IService<UserExtensionEntity> {

    UserExtensionEntity getUserInfo(Long userId, boolean includeRole,boolean includeGroup);

    List<UserExtensionEntity> searchUserList(boolean baseOnCompany,UserSearchParam searchParam);

    Pagination<UserExtensionEntity> getUserList(UserParam param);
}
