package com.bayer.gifts.activiti.factory;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GiftsGroupManagerFactory implements SessionFactory {

    @Autowired
    GiftsGroupEntityManager giftsGroupEntityManager;


    public Session openSession(CommandContext arg0) {
        log.info("GiftsGroupManagerFactory openSession with arg: " + arg0);
        return giftsGroupEntityManager;
    }

    public Class<?> getSessionType() {
        log.info("GiftsGroupManagerFactory getSessionType: ");
        return GiftsGroupEntityManager.class;
    }
}
