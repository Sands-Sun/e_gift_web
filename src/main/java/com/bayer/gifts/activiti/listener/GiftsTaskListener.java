package com.bayer.gifts.activiti.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GiftsTaskListener implements TaskListener {


    private static final long serialVersionUID = 4787661047795203624L;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        switch (eventName){
            case EVENTNAME_CREATE:
                create(delegateTask);
                break;
            case EVENTNAME_ASSIGNMENT:
                assigment(delegateTask);
                break;
            case EVENTNAME_COMPLETE:
                complete(delegateTask);
                break;
            case EVENTNAME_DELETE:
                delete(delegateTask);
                break;
            default:
                break;
        }
    }


    public void create(DelegateTask delegateTask){
        log.info("============TaskListener start============");
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String eventName = delegateTask.getEventName();
        String executeId = delegateTask.getExecutionId();
        DelegateExecution execution =  delegateTask.getExecution();
        String description =  delegateTask.getDescription();

        log.info("事件名称:" + eventName);
        log.info("taskDefinitionKey:" + taskDefinitionKey);
        log.info("executeId:" + executeId);
        log.info("description:" + description);

        log.info("============TaskListener end============");

    }

    public void assigment(DelegateTask delegateTask){
        log.info("============TaskListener start============");
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String eventName = delegateTask.getEventName();
        log.info("事件名称:" + eventName);
        log.info("taskDefinitionKey:" + taskDefinitionKey);
        log.info("============TaskListener end============");
    }

    public void complete(DelegateTask delegateTask){
        log.info("============TaskListener start============");
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String eventName = delegateTask.getEventName();
        String executeId = delegateTask.getExecutionId();
        DelegateExecution execution =  delegateTask.getExecution();
        String description =  delegateTask.getDescription();

        log.info("事件名称:" + eventName);
        log.info("taskDefinitionKey:" + taskDefinitionKey);
        log.info("executeId:" + executeId);
        log.info("description:" + description);

        log.info("============TaskListener end============");
    }


    public void delete(DelegateTask delegateTask){
        log.info("============TaskListener start============");
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String eventName = delegateTask.getEventName();
        String executeId = delegateTask.getExecutionId();
        DelegateExecution execution =  delegateTask.getExecution();
        String description =  delegateTask.getDescription();

        log.info("事件名称:" + eventName);
        log.info("taskDefinitionKey:" + taskDefinitionKey);
        log.info("executeId:" + executeId);
        log.info("description:" + description);
        log.info("============TaskListener end============");
    }

}
