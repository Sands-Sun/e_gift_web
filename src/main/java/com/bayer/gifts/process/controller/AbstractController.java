package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.entity.UserExtensionEntity;
import org.apache.shiro.SecurityUtils;

public abstract class AbstractController {

    protected UserExtensionEntity getUser() {
        return (UserExtensionEntity) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getUserId() {
        return getUser().getSfUserId();
    }
}
