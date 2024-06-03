package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.sys.dao.RoleDao;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.service.RouterService;
import com.bayer.gifts.process.sys.dao.RouterDao;
import com.bayer.gifts.process.sys.dao.RouterMetaDao;
import com.bayer.gifts.process.sys.entity.RouterEntity;
import com.bayer.gifts.process.sys.entity.RouterMetaEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RouterServiceImpl implements RouterService {
    @Autowired
    RouterDao routerDao;

    @Autowired
    RouterMetaDao routerMetaDao;
    @Autowired
    RoleDao roleDao;
    @Override
    public List<RouterEntity> getRoutes(Long userId) {
        List<RouterEntity> result = new ArrayList<>();
        //获取用户权限
        List<RoleEntity> userRoles = roleDao.queryRoleByUserId(userId);
        String userRouteIds = userRoles.stream().map(RoleEntity::getFunctions).collect(Collectors.joining(","));
        if(StringUtils.isEmpty(userRouteIds)){
            userRouteIds= ManageConfig.DEFAULT_ROUTERS;
        }
        List<Integer> userRouterIdList = Arrays.stream(userRouteIds.split(","))
                .map(Integer::valueOf).distinct()
                .collect(Collectors.toList());

        // 获得路由信息
        List<RouterEntity> routers = routerDao.selectList(new QueryWrapper<RouterEntity>().eq("status", "1"));
        Map<Integer, RouterEntity> routerMap = routers.stream().
                collect(Collectors.toMap(
                        RouterEntity::getId,
                        obj -> obj,
                        (key1, key2) -> key1
                ));

        List<RouterMetaEntity> routerMetas = routerMetaDao.selectList(null);
        routerMetas.forEach(e->e.setOrder(e.getOrderId()));
        Map<Integer, RouterMetaEntity> routerMetaMap = routerMetas.stream().
                collect(Collectors.toMap(
                        RouterMetaEntity::getRouterId,
                        obj -> obj,
                        (key1, key2) -> key1
                ));
        for (RouterEntity router : routers) {

            if (userRouterIdList.contains(router.getId())) {
                router.setMeta(routerMetaMap.get(router.getId()));
                if (router.getPId() == -1) {
                    //一级目录跳过处理
                    result.add(router);
                } else {
                    RouterEntity parentRouter = routerMap.get(router.getPId());
                    parentRouter.getChildren().add(router);

                }
            }
        }
        return result;
    }

    @Override
    public List<RouterEntity> getAllRouters() {

        RouterEntity routerEntity = new RouterEntity();
        routerEntity.setName("Menu");
        routerEntity.setId(-1);

        List<RouterEntity> routerList = routerDao.selectList(new QueryWrapper<RouterEntity>().eq("status", "1"));
        Map<Integer, RouterEntity> routerMap = routerList.stream().
                collect(Collectors.toMap(
                        RouterEntity::getId,
                        obj -> obj,
                        (key1, key2) -> key1
                ));
        for (RouterEntity router : routerList) {
            if (router.getPId() == -1) {
                //一级目录跳过处理
                routerEntity.getChildren().add(router);
//                result.add(router);
            } else {
                RouterEntity parentRouter = routerMap.get(router.getPId());
                parentRouter.getChildren().add(router);

            }
        }
        ArrayList<RouterEntity> list = new ArrayList<>();
        list.add(routerEntity);
        return list;
    }

    @Override
    public Boolean isRouteExist(String routeName) {
        RouterEntity routerEntity = routerDao.selectOne(new QueryWrapper<RouterEntity>().eq("name", routeName).eq("status", "1"));

        if(routerEntity!=null){
            return true;
        }
        return false;
    }

}
