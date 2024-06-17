package com.bayer.gifts.activiti.extension;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.entity.GivingGiftsActivityEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.GivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.service.GiftsBaseService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GiftsNotifMailDelegate extends NotifMailBaseDelegate implements JavaDelegate {

    private Expression notifType;

    private Expression fromGroup;

    @Autowired
    BatchCompleteMailService completeMailService;

    @Autowired
    GivingGiftsApplicationDao giftsApplicationDao;

    @Lazy
    @Autowired
    GiftsBaseService giftsBaseService;


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
        String notifTypeValue = (String) notifType.getValue(execution);
        String fromGroupValue = Objects.isNull(fromGroup) ? "NA" : (String) fromGroup.getValue(execution);
        log.info("notifTypeValue: {}, fromGroupValue: {}", notifTypeValue,fromGroupValue);
        GivingGiftsApplyVariable variable =
                execution.getVariable(Constant.GIFTS_APPLY_GIVING_GIFTS_VARIABLE, GivingGiftsApplyVariable.class);
        GiftsTaskVariable taskVariable = execution.getVariable(Constant.GIFTS_TASK_VARIABLE, GiftsTaskVariable.class);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupUserPair = getGroupInfo(notifTypeValue,variable);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupUserPair = getGroupInfo(fromGroupValue,variable);
        variable.setCurrentGroupUserPair(currentGroupUserPair);
        variable.setFromGroupUserPair(fromGroupUserPair);
        variable.setNotifTypeValue(notifTypeValue);
        setStatus(notifTypeValue,variable.getApplicationId(), variable.getCurrentGiftGroup());
        giftsBaseService.setSignatureAndRemark(variable,Constant.GIFTS_GIVING_TYPE);
        setHistoryGroups(variable,currentGroupUserPair);
        GivingGiftsProcessNoticeMailVo givingNoticeMailVo =
                new GivingGiftsProcessNoticeMailVo(variable,taskVariable,execution.getId());
        completeMailService.completeAndSentMail(givingNoticeMailVo);
//        execution.setVariable("completeMail", completeMail);
        execution.setVariable("applyGivingGiftsVar", variable);

    }


    private void setStatus(String notifTypeValue,Long applicationId, GiftsGroupEntity currentGroup) {
        String status = getForApprovalStatus(notifTypeValue,currentGroup);
        if(StringUtils.isNotEmpty(status)){
            giftsApplicationDao.update(null, Wrappers.<GivingGiftsApplicationEntity>lambdaUpdate()
                    .set(GivingGiftsApplicationEntity::getStatus, status)
                    .eq(GivingGiftsApplicationEntity::getApplicationId,applicationId));
        }
    }

}
