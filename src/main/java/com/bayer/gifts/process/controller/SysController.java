package com.bayer.gifts.process.controller;



import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.service.LoadResourceService;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.entity.RouterEntity;
import com.bayer.gifts.process.sys.param.RoleParam;
import com.bayer.gifts.process.sys.service.RoleService;
import com.bayer.gifts.process.sys.service.RouterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "sys")
@Api(tags = "系统管理")
public class SysController extends AbstractController{


    @Autowired
    RoleService roleService;
    @Autowired
    RouterService routerService;

    @Autowired
    LoadResourceService loadResourceService;
    @ApiOperation("刷新用户组")
    @RequestMapping(value = "refresh-userGroup", method = RequestMethod.GET)
    public R refreshUserGroup(@RequestParam(value = "groupId", required = false) String groupId) {
        if(StringUtils.isEmpty(groupId)){
            loadResourceService.loadGiftsGroup();
        }else {
            loadResourceService.refreshGiftGroup(groupId);
        }
        return R.ok();
    }

    @ApiOperation("刷新字典表")
    @RequestMapping(value = "refresh-dictionary", method = RequestMethod.GET)
    public R refreshDictionary() {
        loadResourceService.loadGiftsDictionary();
        return R.ok();
    }

    @ApiOperation("刷新邮件模板")
    @RequestMapping(value = "refresh-mailPolicy", method = RequestMethod.GET)
    public R refreshMailPolicy() {
        loadResourceService.loadMailPolicy();
        return R.ok();
    }


    @ApiOperation("分页查询角色")
    @RequestMapping(value = "/getRoles", method = RequestMethod.POST)
    public R<Pagination<RoleEntity>> getRoles(@RequestBody RoleParam param) {
        return R.ok(roleService.getAllRoles(param));
    }

    @ApiOperation("查询路由菜单")
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    public R<List<RouterEntity>> getRouters() {
        return R.ok(routerService.getAllRouters());
    }

    @ApiOperation("查询路由菜单")
    @RequestMapping(value = "/isRouteExist", method = RequestMethod.GET)
    public Boolean isRouteExist(@RequestParam("routeName") String routeName) {
        return routerService.isRouteExist(routeName);
    }



    @ApiOperation("新增和修改角色")
    @RequestMapping(value = "/saveRole", method = RequestMethod.POST)
    public R<String> saveRole(@RequestBody RoleParam roleParam) {
        Long userId = getUserId();
        roleParam.setUserId(String.valueOf(userId));
        roleService.saveRole(roleParam);
        return R.ok("success","success");
    }

    @ApiOperation("删除角色")
    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    public R<String> deleteRole(@RequestBody List<String> ids) {
        roleService.deleteRoleByIds(ids);
        return R.ok("success","success");
    }
}
