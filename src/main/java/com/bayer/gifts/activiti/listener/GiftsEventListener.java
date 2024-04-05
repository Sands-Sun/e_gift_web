package com.bayer.gifts.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GiftsEventListener implements ActivitiEventListener{


    @Override
    public void onEvent(ActivitiEvent event) {
        log.info("begin gifts event listener >>>>>>>>>>>>>>>");
        switch (event.getType()) {

            case JOB_EXECUTION_SUCCESS:
                System.out.println("A job well done!");
                String executeId = event.getExecutionId();

                break;

            case JOB_EXECUTION_FAILURE:
                System.out.println("A job has failed...");
                break;

            default:
                System.out.println("Event received: " + event.getType());
        }
        log.info("end gifts event listener >>>>>>>>>>>>>>>");
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

}
