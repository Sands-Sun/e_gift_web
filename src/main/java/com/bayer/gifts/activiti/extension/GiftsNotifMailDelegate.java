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
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GiftsNotifMailDelegate implements JavaDelegate {

    private Expression notifType;

    private Expression fromGroup;

    @Autowired
    BatchCompleteMailService completeMailService;

    @Autowired
    GivingGiftsApplicationDao giftsApplicationDao;

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
        setStatus(notifTypeValue,variable.getApplicationId(), variable.getCurrentGiftGroup());
        giftsBaseService.setSignatureAndRemark(variable,Constant.GIFTS_GIVING_TYPE);
        setHistoryGroups(variable,currentGroupUserPair);
        GivingGiftsProcessNoticeMailVo givingNoticeMailVo =
                new GivingGiftsProcessNoticeMailVo(variable,taskVariable,execution.getId());
        BatchCompleteMail completeMail =
                completeMailService.saveCompleteMail(givingNoticeMailVo);
        if(Objects.nonNull(completeMail)){
            try {
                MailUtils.sendMail(completeMail.getMailTo(),
                        completeMail.getMailSubject(),completeMail.getMailBody(), null);
            } catch (MessagingException | IOException e) {
                log.error("send mail error",e);
            }
        }
//        execution.setVariable("completeMail", completeMail);
        execution.setVariable("applyGivingGiftsVar", variable);

    }

    private void setHistoryGroups(GivingGiftsApplyVariable variable,
                                  Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupUserPair) {
        if(Objects.isNull(currentGroupUserPair) || Objects.isNull(currentGroupUserPair.getKey())) {
            log.info("empty current group");
            return;
        }
        GiftsGroupEntity currentGroup = currentGroupUserPair.getKey();
        List<GiftsUserToGroupEntity> groupUsers = currentGroupUserPair.getRight();
        variable.setHisProcessGroups(currentGroup.getGroupCode(), groupUsers);
    }

    private void setStatus(String notifTypeValue,Long applicationId, GiftsGroupEntity currentGroup) {
        log.info("set waiting for status....");
        if(Constant.GIFTS_REQUESTER.equals(notifTypeValue)){
            log.info("notify requester no need set status...");
            return;
        }
        String groupFullName = currentGroup.getFullName();
        // Approve status
        String status = String.format("For %s Approval",groupFullName);
        giftsApplicationDao.update(null, Wrappers.<GivingGiftsApplicationEntity>lambdaUpdate()
                .set(GivingGiftsApplicationEntity::getStatus, status)
                .eq(GivingGiftsApplicationEntity::getApplicationId,applicationId));
    }



    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> getGroupInfo(String typeValue, GivingGiftsApplyVariable variable) {
        log.info("notification type: {}", typeValue);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> pair = null;
        if(StringUtils.isEmpty(typeValue) || "NA".equals(typeValue)){
            log.info("empty typeValue: {}", typeValue);
            return null;
        }
        switch (typeValue) {
            case Constant.GIFTS_REQUESTER: {
                GiftsGroupEntity group = new GiftsGroupEntity();
                group.setFullName(StringUtils.EMPTY);
                GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
                userToGroup.setUserId(variable.getApplyForId());
                userToGroup.setUserEmail(variable.getApplyEmail());
                pair = Pair.of(group, Collections.singletonList(userToGroup));
                break;
            }
            case Constant.GIFTS_LEADERSHIP_LINE_MANAGER: {
                GiftsGroupEntity group = new GiftsGroupEntity();
                group.setFullName("Line Manager");
                GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
                userToGroup.setUserId(variable.getSupervisorId());
                userToGroup.setUserEmail(variable.getSupervisorMail());
                pair = Pair.of(group, Collections.singletonList(userToGroup));
                break;
            }
            case Constant.GIFTS_LEADERSHIP_SOC_GROUP:
                pair = variable.getScoGroupUserPair();
                break;
            case Constant.GIFTS_LEADERSHIP_DEPARTMENT_HEAD:
                pair = variable.getDepartmentHeadGroupUserPair();
                break;
            case Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD:
                pair = variable.getCountryHeadGroupUserPair();
                break;
        }
        return pair;
    }
}
