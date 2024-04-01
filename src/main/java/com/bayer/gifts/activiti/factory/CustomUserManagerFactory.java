package com.bayer.gifts.activiti.factory;

import com.bayer.gifts.activiti.management.CustomUserEntityManager;
import com.bayer.gifts.process.dao.UserExtensionDao;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "customUserManagerFactory")
public class CustomUserManagerFactory implements SessionFactory {


    @Autowired
    UserExtensionDao userExtensionDao;
    @Autowired
    ProcessEngineConfigurationImpl processEngineConfiguration;

    @Override
    public Class<?> getSessionType() {
        System.out.println("CustomGroupManagerFactory getSessionType: ");
        return CustomUserEntityManager.class;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        System.out.println("CustomUserManagerFactory openSession with arg: " + commandContext);
        CustomUserEntityManager userManager =
                new CustomUserEntityManager(processEngineConfiguration,userExtensionDao);
        return (Session) userManager;
    }
}
