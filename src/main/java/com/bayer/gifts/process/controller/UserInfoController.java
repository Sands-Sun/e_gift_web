package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "sys/user")
public class UserInfoController extends AbstractController{

    @Autowired
    UserInfoService userInfoService;


    @ApiOperation("根据Id获得用户信息")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public R<UserExtensionEntity> get(@PathVariable Long id,
                                      @RequestParam(value = "includeRole",
                                              defaultValue = "false") boolean includeRole) {
        return R.ok(userInfoService.getUserInfo(id,includeRole));
    }
}
