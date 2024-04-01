package com.bayer.gifts.process.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
public class LoginController {

    @Autowired
    UserExtensionDao userExtensionDao;

    @Autowired
    SysUserTokenService sysUserTokenService;

//    @RequestMapping(value = "/login")
//    public R login(String username, String password) {
//        //获取一个用户
//        Subject subject = SecurityUtils.getSubject();
//        //封装用户登录的数据
//        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
//        try{
//            subject.login(token);
//            return  R.ok("success");
//        }catch (UnknownAccountException e){//用户名不存在
//            return R.error("not found username");
//        }catch (IncorrectCredentialsException e){//密码不存在
//            return R.error("password error");
//        }
//    }

    @RequestMapping(value = "/sys/login", method = {RequestMethod.POST, RequestMethod.GET})
    public R<SysUserTokenEntity> sysLogin(@RequestBody Map<String, String> param) {
        if (!param.containsKey("CWID")) {
            return R.error("参数异常!");
        }
        String cwid = param.get("CWID");
        UserExtensionEntity user = userExtensionDao.selectOne(Wrappers.<UserExtensionEntity>lambdaQuery()
                .eq(UserExtensionEntity::getCwid, cwid));
        if (Objects.isNull(user)) {
            log.info("不存在用户 cwid: {}", cwid);
            return R.error("用户不存在系统!");
        }
        SysUserTokenEntity token = sysUserTokenService.createUserToken(user.getSfUserId());
        return R.ok(token);
    }


    /**
     *  使用azure 身份验证，如果参数为空，获取重定向地址，
     *  如果参数不为空，则获取
     * @param id
     * @return
     */
    @GetMapping("/sys/azureLogin")
    public R getAzureLoginUrl(String id) {
        return R.ok("success");
    }
}
