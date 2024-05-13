package com.bayer.gifts.process.controller;



import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.entity.RouterEntity;
import com.bayer.gifts.process.sys.service.RoleService;
import com.bayer.gifts.process.sys.service.RouterService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "sys")
public class SysController extends AbstractController{


    @Autowired
    RoleService roleService;
    @Autowired
    RouterService routerService;


    @ApiOperation("分页查询角色")
    @RequestMapping(value = "/getRoles", method = RequestMethod.POST)
    public R<Pagination<RoleEntity>> getRoles(@RequestBody Map<String,Object> param) {
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
    public R<String> saveRole(@RequestBody Map<String,Object> params) {
        Long userId = getUserId();
        roleService.saveRole(params,userId);
        return R.ok("success","success");
    }

    @ApiOperation("删除角色")
    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    public R<String> deleteRole(@RequestBody List<String> ids) {
        roleService.deleteRoleByIds(ids);
        return R.ok("success","success");
    }
}
