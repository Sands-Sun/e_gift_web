package com.bayer.gifts.activiti.listener;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.bayer.gifts.process.variables.GivingHospApplyVariable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class GiftsTaskListener implements TaskListener {


    private static final long serialVersionUID = 4787661047795203624L;

    @Autowired
    TaskService taskService;

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
        String definitionId = execution.getProcessDefinitionId();
        String description =  delegateTask.getDescription();
        String taskId = delegateTask.getId();
        if(definitionId.startsWith(Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX)){
            GivingGiftsApplyVariable applyVariable =
                    execution.getVariable(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE, GivingGiftsApplyVariable.class);
//            if(applyVariable.isSkipDeptHead() || applyVariable.isSkipCountryHead()){
//                Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//                if(Objects.isNull(task)){
//                    log.info("not found task...");
//                    return;
//                }
//                Map<String, Object> variables = Maps.newHashMap();
//                GiftsTaskVariable taskVariable = new GiftsTaskVariable();
//                taskVariable.setUserId(applyVariable.getSupervisorId());
////                taskVariable.setComment("System auto");
//                taskVariable.setApprove(Constant.GIFTS_APPROVE_TYPE);
//                taskService.claim(taskId,String.valueOf(applyVariable.getSupervisorId()));
//                taskService.complete(taskId, variables);
//            }

        }else if(definitionId.startsWith(Constant.GIVING_HOSP_PROCESS_TYPE_PREFIX)){
        }



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
