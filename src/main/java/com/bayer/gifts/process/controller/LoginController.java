package com.bayer.gifts.process.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.Prompt;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.ResponseMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@RestController
public class LoginController {

    @Value("${aad.clientId}")
    private String clientId;
    @Value("${aad.authority}")
    private String authority;
    @Value("${aad.redirectUriSignin}")
    private String redirectUriSignin;
    @Value("${aad.redirecLogin}")
    private String redirecLogin;
    @Autowired
    UserExtensionDao userExtensionDao;

    @Autowired
    SysUserTokenService sysUserTokenService;

    @RequestMapping(value = "/sys/login", method = {RequestMethod.POST, RequestMethod.GET})
    public R<SysUserTokenEntity> sysLogin(@RequestBody Map<String, String> param) {
        if (!param.containsKey("CWID")) {
            return R.error("参数异常!");
        }
        String cwid = param.get("CWID");
        UserExtensionEntity user = userExtensionDao.selectOne(Wrappers.<UserExtensionEntity>lambdaQuery()
                .eq(UserExtensionEntity::getCwid, cwid)
                .eq(UserExtensionEntity::getMarkDeleted, Constant.NO_EXIST_MARK));
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
     * @return
     */
    @GetMapping("/sys/azureLogin")
    public R getAzureLoginUrl() {

        String state = UUID.randomUUID().toString();
        PublicClientApplication pca=null;
        String updatedScopes =  "";

        try {
            pca = PublicClientApplication.builder(clientId).authority(authority).build();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AuthorizationRequestUrlParameters parameters =
                AuthorizationRequestUrlParameters
                        .builder(redirectUriSignin,
                                Collections.singleton(updatedScopes))
                        .responseMode(ResponseMode.QUERY)
                        .prompt(Prompt.SELECT_ACCOUNT)
                        .state(state)
                        .claimsChallenge("")
                        .build();

        String url = pca.getAuthorizationRequestUrl(parameters).toString();
        return R.ok("success",url);
    }

    @GetMapping("/sys/azure/getToken")
    public void getAzureToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = sysUserTokenService.saveAzureToken(request, response);
        response.sendRedirect(redirecLogin+"?token="+token);


    }
}
