package com.bayer.gifts.process.service.impl;

import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.service.GiftsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userService")
public class GiftsUserServiceImpl implements GiftsUserService {

    @Autowired
    UserExtensionDao userExtensionDao;


}
