package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.sys.dao.RoleDao;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleDao roleDao;


    @Override
    public Pagination<RoleEntity> getAllRoles(Map<String, Object> param) {
        Object roleName = param.get("roleName");
        IPage<RoleEntity> pageInfo= roleDao.selectAll(new Page<>((Integer)param.get("current"),(Integer)param.get("pageSize")),param.get("roleName")==null? null:(String)param.get("roleName"),param.get("status")==null?null:(String)param.get("status"));

        return new Pagination<>(pageInfo);
    }

    @Override
    public void saveRole(Map<String, Object> roleEntity, Long userId) {

        RoleEntity role = new RoleEntity();
        role.setRoleName((String)roleEntity.get("roleName") );
        role.setRemark((String)roleEntity.get("roleDesc"));
        role.setMarkForDelete((String)roleEntity.get("status"));
        role.setFunctions((String)roleEntity.get("function"));

        if(roleEntity.get("id") !=null){
            role.setId((Integer)roleEntity.get("id"));
            role.setUpdateBy(userId);
            role.setUpdateDate(new Date());
            roleDao.updateById(role);
        }else {
            role.setCreateDate(new Date());
            role.setCreateBy(userId);
            roleDao.insert(role);

        }


    }

    @Override
    public void deleteRoleByIds(List<String> ids) {
        roleDao.deleteBatchIds(ids);
    }
}
