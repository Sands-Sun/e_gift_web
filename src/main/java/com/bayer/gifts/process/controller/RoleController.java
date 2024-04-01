package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.param.GiftsGroupParam;
import com.bayer.gifts.process.param.GiftsRoleParam;
import com.bayer.gifts.process.service.GiftsRoleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "role")
public class RoleController extends AbstractController{

    @Autowired
    GiftsRoleService giftsRoleService;


    @ApiOperation("保存用户组")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(/*@Validated({AddGroup.class, Default.class})*/ @RequestBody GiftsRoleParam param) {
        giftsRoleService.saveGiftsRole(param);
        return R.ok();
    }
}
