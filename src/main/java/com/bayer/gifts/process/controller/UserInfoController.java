package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserSearchParam;
import com.bayer.gifts.process.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "sys/user")
public class UserInfoController extends AbstractController{

    @Autowired
    UserInfoService userInfoService;


    @ApiOperation("根据Id获得用户信息")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public R<UserExtensionEntity> get(@PathVariable Long id,
                                      @RequestParam(value = "includeRole", defaultValue = "false") boolean includeRole,
                                      @RequestParam(value = "includeGroup", defaultValue = "false") boolean includeGroup) {
        return R.ok(userInfoService.getUserInfo(id,includeRole,includeGroup));
    }

    @ApiOperation("获得用户信息")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public R<UserExtensionEntity> getUserInfo() {
        UserExtensionEntity user = getUser();
        return R.ok(user);
    }

    @ApiOperation("模糊搜索用户列表")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public R<List<UserExtensionEntity>> searchUserList(@RequestParam(required = false, value = "baseOnCompany") boolean baseOnCompany,
                                                       UserSearchParam searchParam) {
        return R.ok(userInfoService.searchUserList(baseOnCompany,searchParam));
    }
}
