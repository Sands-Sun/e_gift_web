package com.bayer.gifts.activiti.extension;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GivingHospitalityApplicationDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.entity.HospitalityApplicationEntity;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.GivingHospProcessNoticeMailVo;
import com.bayer.gifts.process.service.GiftsBaseService;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingHospApplyVariable;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class HospNotifMailDelegate extends NotifMailBaseDelegate implements JavaDelegate {

    private Expression notifType;

    private Expression fromGroup;

    @Autowired
    BatchCompleteMailService completeMailService;

    @Autowired
    GivingHospitalityApplicationDao hospitalityApplicationDao;

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
        GivingHospApplyVariable variable =
                execution.getVariable(Constant.GIFTS_APPLY_GIVING_HOSP_VARIABLE, GivingHospApplyVariable.class);
        GiftsTaskVariable taskVariable = execution.getVariable(Constant.GIFTS_TASK_VARIABLE, GiftsTaskVariable.class);
        Pair<GiftsGroupEntity, List<GiftsUserToGroupEntity>> currentGroupUserPair = getGroupInfo(notifTypeValue,variable);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupUserPair = getGroupInfo(fromGroupValue,variable);
        variable.setCurrentGroupUserPair(currentGroupUserPair);
        variable.setFromGroupUserPair(fromGroupUserPair);
        setStatus(notifTypeValue,variable.getApplicationId(), variable.getCurrentGiftGroup());
        giftsBaseService.setSignatureAndRemark(variable,Constant.HOSPITALITY_TYPE);
        setHistoryGroups(variable,currentGroupUserPair);
        GivingHospProcessNoticeMailVo hospNoticeMailVo =
                new GivingHospProcessNoticeMailVo(variable,taskVariable,execution.getId());
        BatchCompleteMail completeMail =
                completeMailService.saveCompleteMail(hospNoticeMailVo);
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

    private void setStatus(String notifTypeValue,Long applicationId, GiftsGroupEntity currentGroup) {
        String status = getForApprovalStatus(notifTypeValue,currentGroup);
        hospitalityApplicationDao.update(null, Wrappers.<HospitalityApplicationEntity>lambdaUpdate()
                .set(HospitalityApplicationEntity::getStatus, status)
                .eq(HospitalityApplicationEntity::getApplicationId,applicationId));
    }
}
