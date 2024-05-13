package com.bayer.gifts.activiti.listener;


import com.bayer.gifts.process.service.GiftsBaseService;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GiftsEventListener implements ActivitiEventListener{


    @Autowired
    GiftsBaseService giftsBaseService;
    @Override
    public void onEvent(ActivitiEvent event) {
        log.info("begin gifts event listener >>>>>>>>>>>>>>>");
        switch (event.getType()) {
            case PROCESS_COMPLETED:
                log.info("process completed >>>> {}", event);
                giftsBaseService.documentGiftsProcess(event);
                break;
            case JOB_EXECUTION_SUCCESS:
                log.info("A job well done!");
                break;

            case JOB_EXECUTION_FAILURE:
                log.info("A job has failed...");
                break;
            default:
                log.info("Event received: " + event.getType());
        }
        log.info("end gifts event listener >>>>>>>>>>>>>>>");
    }



    @Override
    public boolean isFailOnException() {
        return false;
    }

}
