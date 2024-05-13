package com.bayer.gifts.activiti.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GiftsExecutionListener implements ExecutionListener{

    private static final long serialVersionUID = 7760633747386972372L;

    private Expression needApprove;

    @Autowired
    GivingGiftsApplicationDao giftsApplicationDao;


    @Override
    public void notify(DelegateExecution execution) {
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
           String eventName = execution.getEventName();
           switch (eventName) {
               case EVENTNAME_START:
                   start(execution);
                   break;
               case EVENTNAME_END:
                   end(execution);
                   break;
               case EVENTNAME_TAKE:
                   take(execution);
                   break;
               default:
                   break;
           }

    }


    public void take(DelegateExecution execution){
        log.info("============ExecutionListener take START============");
        log.info("execition name: {}", execution.getEventName());
        log.info("============ExecutionListener take END============");

    }
    public void start(DelegateExecution execution){
        log.info("============ExecutionListener START============");
        log.info("execition name: {}", execution.getEventName());
        log.info("============ExecutionListener END============");

    }
    public void end(DelegateExecution execution){
        log.info("============ExecutionListener end START============");
        String needApproveValue = (String) needApprove.getValue(execution);
        log.info("needApproveValue: {}", needApproveValue);
        GivingGiftsApplyVariable applyVariable =
                execution.getVariable("applyGivingGiftsVar", GivingGiftsApplyVariable.class);
        GiftsTaskVariable taskVariable = execution.getVariable("taskVariable", GiftsTaskVariable.class);
//        String actionType = applyVariable.getActionType();
        Long applicationId = applyVariable.getApplicationId();
        GiftsGroupEntity currentGroup = applyVariable.getCurrentGiftGroup();
        if(Objects.nonNull(taskVariable) && StringUtils.isNotEmpty(needApproveValue) &&
                Boolean.parseBoolean(needApproveValue)){
            String actionType = taskVariable.getApprove();
            String groupFullName = currentGroup.getFullName();
          // Approve status
           String status = String.format("%s %s",groupFullName, actionType);
           log.info("approve status >>>> {}",status);
           if(Constant.GIFTS_REJECTED_TYPE.equals(actionType)){
               giftsApplicationDao.update(null, Wrappers.<GivingGiftsApplicationEntity>lambdaUpdate()
                       .set(GivingGiftsApplicationEntity::getStatus, actionType)
                       .eq(GivingGiftsApplicationEntity::getApplicationId,applicationId));
           }

            applyVariable.setActionType(actionType);
            execution.setVariable("applyGivingGiftsVar", applyVariable);
        }

        log.info("============ExecutionListener end END============");

    }

}
