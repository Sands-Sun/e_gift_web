package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bayer.gifts.process.sys.dao.RouterDao;
import com.bayer.gifts.process.sys.dao.RouterMetaDao;
import com.bayer.gifts.process.sys.dao.UserToRoleDao;
import com.bayer.gifts.process.sys.entity.RouterEntity;
import com.bayer.gifts.process.sys.entity.RouterMetaEntity;
import com.bayer.gifts.process.sys.service.RouterService;
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
    private RouterDao routerDao;

    @Autowired
    private RouterMetaDao routerMetaDao;
    @Autowired
    private UserToRoleDao userToRoleDao;
    @Override
    public List<RouterEntity> getRoutes(Long userId) {
        List<RouterEntity> result = new ArrayList<>();
        //获取用户权限
        String userRouteIds =userToRoleDao.selectUserRouteIds(userId);
        if(StringUtils.isEmpty(userRouteIds)){
            userRouteIds="0";
        }
        List<Integer> userRouterIdList = Arrays.asList(userRouteIds.split(",")).stream()
                .map(Integer::valueOf)
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
        routerMetas.stream().forEach(e->e.setOrder(e.getOrderId()));
        Map<Integer, RouterMetaEntity> routerMetaMap = routerMetas.stream().
                collect(Collectors.toMap(
                RouterMetaEntity::getRouterId,
                obj -> obj,
                (key1, key2) -> key1
        ));
        for (RouterEntity router : routers) {

            if (userRouterIdList.contains(router.getId())) {
                router.setMeta(routerMetaMap.get(router.getId()));
                if (router.getPId() == 0) {
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

}
