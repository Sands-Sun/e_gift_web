package com.bayer.gifts.activiti.extension;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GiftsNotifMailDelegate implements JavaDelegate {


    private Expression notifType;


    @Autowired
    BatchCompleteMailService completeMailService;


    @Override
    public void execute(DelegateExecution execution) {
        log.info("[Process=" + execution.getProcessInstanceId() +
                "][Java Delegate=" + this + "]");
        log.info("SuperExecutionId: " + execution.getSuperExecutionId());
        log.info("CurrentActivityId: " + execution.getCurrentActivityId());
        log.info("ParentId: " + execution.getParentId());
        log.info("ProcessInstanceBusinessKey" +
                execution.getProcessInstanceBusinessKey());
        log.info("ProcessDefinitionId: " + execution.getProcessDefinitionId());
        log.info("EventName" + execution.getEventName());
        log.info("RootProcessInstanceId" + execution.getRootProcessInstanceId());

        GivingGiftsApplyVariable applyVar = execution.getVariable("applyGivingGiftsVar",
                GivingGiftsApplyVariable.class);
        String notifTypeValue = (String) notifType.getValue(execution);
        if(Constant.GIFTS_LEADERSHIP_LINE_MANAGER.equals(notifTypeValue)){

        }else if(Constant.GIFTS_LEADERSHIP_SOC_GROUP.equals(notifTypeValue)){

        }else if(Constant.GIFTS_LEADERSHIP_DEPARTMENT_HEAD.equals(notifTypeValue)){
            
        } else if (Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD.equals(notifTypeValue)) {
            
        }
    }
}
