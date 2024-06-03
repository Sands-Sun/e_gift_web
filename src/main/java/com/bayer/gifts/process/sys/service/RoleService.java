package com.bayer.gifts.process.sys.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.param.RoleParam;


import java.util.List;
import java.util.Map;

public interface RoleService {
    Pagination<RoleEntity> getAllRoles(RoleParam param);

     void saveRole(RoleParam param);

     void deleteRoleByIds(List<String> ids);
}
