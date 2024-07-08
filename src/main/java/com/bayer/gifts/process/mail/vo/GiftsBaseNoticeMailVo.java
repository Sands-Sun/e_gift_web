package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.entity.GiftsActivityBaseEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Slf4j
public class GiftsBaseNoticeMailVo extends NoticeMailVo{
    @MailContentFieldIgnore(value = true)
    protected GiftsTaskVariable taskVariable;

    protected String applyForName;
    protected String creatorName;
    protected String applyDate;
    protected String supervisorName;
    protected String reason;
    protected String referenceNo;
    protected String companyCode;
    private String notifTypeValue;
    protected List<String> signatureList;
    protected List<String> remarkList;

    public GiftsBaseNoticeMailVo() {

    }

    public GiftsBaseNoticeMailVo(String processType, String mailType) {
        super();
        this.setProcessType(processType);
        this.setMailType(mailType);
    }
    public GiftsBaseNoticeMailVo(GiftsTaskVariable taskVariable, String processType, String mailType) {
        super();
        this.taskVariable = taskVariable;
//        this.setAutoSent(false);
//        this.setProcessType("Gift");
//        this.setMailType(Constant.GIFTS_GIVING_TYPE);
        this.setProcessType(processType);
        this.setMailType(mailType);
//        this.setActionType(applyVariable.getActionType());
//        this.setMailSender(this.getCompanyCode() + "_" + processType + "_");
    }


    public void resetSignatureAndRemark(GiftsApplyBaseVariable applyVariable) {
        this.setSignatureList(applyVariable.getSignatureList());
        this.setRemarkList(applyVariable.getRemarkList());
    }

    protected void fillInHisProcessUsers(GiftsApplyBaseVariable applyVariable) {
        this.setMailTo(StringUtils.EMPTY);
        this.setMailCc(StringUtils.EMPTY);
        List<? extends GiftsActivityBaseEntity> activityList = applyVariable.getActivityList();
        List<String> mailToHisUsers = activityList.stream().map(GiftsActivityBaseEntity::getUserEmail).collect(Collectors.toList());
        log.info("mailTo history mailToHisUsers: {}", mailToHisUsers);
        List<String> applyList = Stream.of(applyVariable.getCreatorEmail(),applyVariable.getApplyEmail())
                .collect(Collectors.toList());
        log.info("mailTo apply and creator: {}", applyList);
        List<String> mailToList = Stream.of(mailToHisUsers,applyList).flatMap(Collection::stream)
                .distinct().collect(Collectors.toList());
        List<String> mailCcList = applyVariable.getCopyToUserEmails();
        if(CollectionUtils.isNotEmpty(mailToList)){
            this.setMailTo(String.join(";", mailToList));
        }
        if(CollectionUtils.isNotEmpty(mailCcList)){
            this.setMailCc(String.join(";", mailCcList));
        }
        log.info("mailToList: {}, mailCcList: {}", mailToList, mailCcList);
    }
}
