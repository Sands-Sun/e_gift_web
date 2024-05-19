package com.bayer.gifts.activiti.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.dao.GivingHospitalityApplicationDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.entity.HospitalityApplicationEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.bayer.gifts.process.variables.GivingHospApplyVariable;
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

    @Autowired
    GivingHospitalityApplicationDao hospitalityApplicationDao;


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
        String definitionId = execution.getProcessDefinitionId();
        log.info("definitionId >>>> {}", definitionId);
        if(definitionId.startsWith(Constant.GIVING_GIFTS_PROCESS_TYPE_PREFIX)){
            updateApplicationStatus(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE, execution, GivingGiftsApplyVariable.class);
        }else if(definitionId.startsWith(Constant.GIVING_HOSP_PROCESS_TYPE_PREFIX)){
            updateApplicationStatus(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE, execution, GivingHospApplyVariable.class);
        }
        log.info("============ExecutionListener end END============");

    }

    private <T> void updateApplicationStatus(String applyVariableType,DelegateExecution execution, Class<T> tClass) {
        String needApproveValue = (String) needApprove.getValue(execution);
        log.info("needApproveValue: {}", needApproveValue);
        T t = execution.getVariable(applyVariableType, tClass);
        GiftsTaskVariable taskVariable = execution.getVariable(Constant.GIFTS_TASK_VARIABLE, GiftsTaskVariable.class);
        GiftsApplyBaseVariable applyVariable = (GiftsApplyBaseVariable)t;

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
                updateApplicationStatus(applyVariableType,applicationId,actionType);
            }
            applyVariable.setActionType(actionType);
            execution.setVariable(applyVariableType, t);

        }
    }
    private void updateApplicationStatus(String applyVariableType, Long applicationId, String actionType) {
        if(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE.equalsIgnoreCase(applyVariableType)){
            giftsApplicationDao.update(null, Wrappers.<GivingGiftsApplicationEntity>lambdaUpdate()
                        .set(GivingGiftsApplicationEntity::getStatus, actionType)
                        .eq(GivingGiftsApplicationEntity::getApplicationId,applicationId));
        }else if(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE.equalsIgnoreCase(applyVariableType)){
            hospitalityApplicationDao.update(null, Wrappers.<HospitalityApplicationEntity>lambdaUpdate()
                    .set(HospitalityApplicationEntity::getStatus, actionType)
                    .eq(HospitalityApplicationEntity::getApplicationId,applicationId));
        }
    }
}
