package com.bayer.gifts.process.sys.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.sys.entity.RoleEntity;


import java.util.List;
import java.util.Map;

public interface RoleService {
    Pagination<RoleEntity> getAllRoles(Map<String, Object> param);

     void saveRole(Map<String, Object> roleEntity, Long userId);

     void deleteRoleByIds(List<String> ids);
}
