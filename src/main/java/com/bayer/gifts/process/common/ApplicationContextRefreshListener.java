package com.bayer.gifts.process.common;


import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.service.LoadResourceService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null){
            event.getApplicationContext().getBean(LoadResourceService.class).load();
            event.getApplicationContext().getBean(BatchCompleteMailService.class).init();
//            event.getApplicationContext().getBean(ReservationBatchCompleteMailService.class).init();
        }
    }
}
