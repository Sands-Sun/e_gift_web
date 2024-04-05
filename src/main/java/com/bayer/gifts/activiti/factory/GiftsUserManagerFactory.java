package com.bayer.gifts.activiti.factory;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class GiftsUserManagerFactory implements SessionFactory {

    @Autowired
    GiftsUserEntityManager giftsUserEntityManager;


    public Session openSession(CommandContext arg0) {
        log.info("GiftsUserManagerFactory openSession with arg: " + arg0);
        return giftsUserEntityManager;
    }

    public Class<?> getSessionType() {
        log.info("GiftsGroupManagerFactory getSessionType: ");
        return GiftsUserEntityManager.class;
    }
}
