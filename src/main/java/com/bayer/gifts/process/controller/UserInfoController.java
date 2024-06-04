package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserParam;
import com.bayer.gifts.process.param.UserSearchParam;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.sys.entity.RouterEntity;
import com.bayer.gifts.process.sys.service.RouterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "sys/user")
@Api(tags = "用户管理")
public class UserInfoController extends AbstractController{

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RouterService routerService;


    @ApiOperation("根据Id获得用户信息")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public R<UserExtensionEntity> get(@PathVariable Long id,
                                      @RequestParam(value = "includeRole", defaultValue = "false") boolean includeRole,
                                      @RequestParam(value = "includeGroup", defaultValue = "false") boolean includeGroup,
                                      @RequestParam(value = "includeSupervisor", defaultValue = "true") boolean includeSupervisor) {
        return R.ok(userInfoService.getUserInfo(id,includeRole,includeGroup,includeSupervisor));
    }

    @ApiOperation("获得用户信息")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public R<UserExtensionEntity> getUserInfo(@RequestParam(value = "token") String token) {
        UserExtensionEntity user= userInfoService.getUserInfoByToken(token);
        return R.ok(user);
    }

    @ApiOperation("模糊搜索用户列表")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public R<List<UserExtensionEntity>> searchUserList(@RequestParam(required = false, value = "baseOnCompany") boolean baseOnCompany,
                                                       @RequestParam(value = "division",defaultValue = "", required = false) String division,
                                                       UserSearchParam searchParam) {
        searchParam.setDivision(division);
        return R.ok(userInfoService.searchUserList(baseOnCompany,searchParam));
    }

    @ApiOperation("获得用户的路由信息")
    @RequestMapping(value = "/getUserRoutes", method = RequestMethod.GET)
    public R getUserRoutes() {
        Long userId = getUserId();
        List<RouterEntity> routers= routerService.getRoutes(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("routes",routers);
        data.put("home","home");
        return R.ok(data);
    }

    @ApiOperation("获得用户列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<UserExtensionEntity>> page(@RequestBody UserParam param) {
        return R.ok(userInfoService.getUserList(param));
    }
}
