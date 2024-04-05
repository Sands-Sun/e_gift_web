package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public class GivingGiftsNoticeMailVo extends NoticeMailVo{
    @MailContentFieldIgnore(value = true)
    private GivingGiftsApplyVariable applyVariable;

    @MailContentFieldIgnore(value = true)
    private GiftsTaskVariable taskVariable;

    private String applyForName;
    private String creatorName;
    private String applyDate;
    private String supervisorName;
    private Integer volume;
    private Double unitValue;
    private Double totalValue;
    private String giftDescType;
    private String giftDesc;
    private String reasonType;
    private String reason;
    private String referenceNo;
    //    private String signature;
    private String givenPersons;
    private String givenCompany;
    private String givenDate;
    private String companyCode;
    //是否是政府官员或国有企业员工
    private String isGoSoc;
    //接受者是否是拜耳现有客户
    private String isBayerCustomer;
    private List<GiftsUserToGroupEntity> scoGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> departmentHeadGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> countryHeadGroupUserList = new ArrayList<>();

    private List<String> signatureList;
    private List<String> remarkList;



    public GivingGiftsNoticeMailVo() {

    }

    public GivingGiftsNoticeMailVo(GivingGiftsApplyVariable applyVariable,
                                   GiftsTaskVariable taskVariable,
                                   String executionId) {
        super();
        this.applyVariable = applyVariable;
        this.taskVariable = taskVariable;
        BeanUtils.copyProperties(applyVariable,this,
                "scoGroupUserList","departmentHeadGroupUserList","countryHeadGroupUserList");
        this.totalValue = applyVariable.getTotalValue();
        this.scoGroupUserList = this.applyVariable.getScoGroupUserList();
        this.departmentHeadGroupUserList = this.applyVariable.getDepartmentHeadGroupUserList();
        this.countryHeadGroupUserList = this.applyVariable.getCountryHeadGroupUserList();
        this.setAutoSent(false);
        this.setProcessType("Gift");
        this.setMailType("Giving");
//        this.setActionType(applyVariable.getActionType());
        this.setMailSender(companyCode + "_gifts_");
        this.setExecutionId(executionId);

        this.fillExtraInfo();
    }



    private void fillExtraInfo() {
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupPair = applyVariable.getCurrentGroupUserPair();
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupPair = applyVariable.getFromGroupUserPair();
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> groupPair =
                Objects.isNull(fromGroupPair) ? currentGroupPair : fromGroupPair;
        log.info("from group is empty ?  {}",Objects.isNull(fromGroupPair));
        GiftsGroupEntity group = groupPair.getKey();
        String groupFullName = group.getFullName();
        this.fillInSubject(groupFullName);
        List<GiftsUserToGroupEntity> userToGroups = currentGroupPair.getValue();
        List<String> mailToList;
        if(Objects.isNull(taskVariable)) {
            mailToList = userToGroups.stream().map(GiftsUserToGroupEntity::getUserEmail)
                    .collect(Collectors.toList());
        } else {
            this.setActionType(taskVariable.getApprove());
            mailToList = userToGroups.stream().filter(u ->
                !u.getUserId().equals(taskVariable.getUserId())
            ).map(GiftsUserToGroupEntity::getUserEmail).collect(Collectors.toList());

            this.setTaskId(taskVariable.getTaskId());
        }
        if(CollectionUtils.isNotEmpty(mailToList)){
           this.setMailTo(String.join(";", mailToList));
        }
        log.info("group: {}, mailToList: {}", groupFullName, mailToList);
    }





    //(是否是政府官员或国有企业员工)isGoSoc --> Yes Subject: recipient is gov. official or SOE employee.
    private String getIsScoMark() {
       return  "recipient is gov. official or SOE employee.";
    }

    private String getPriceRangeMark() {
        //1391 || 2614 below
        //Giving gifts request for your approval: (Total value is below 300 RMB)
        //Giving gifts request for your approval: (Total value is above 500 RMB)
        //Giving gifts request for your approval: (Total value is between 300 RMB and 500 RMB)
        String subjectContent;
        if(totalValue < ManageConfig.GIVING_UNIT_MIN_PRICE){
            subjectContent = " %s" + String.format("(Total value is below %s RMB)",ManageConfig.GIVING_UNIT_MIN_PRICE);
        } else if(totalValue <= ManageConfig.GIVING_UNIT_MAX_PRICE){
            subjectContent = " %s" + String.format("(Total value is between %s RMB and %s RMB)",
                    ManageConfig.GIVING_UNIT_MIN_PRICE,ManageConfig.GIVING_UNIT_MAX_PRICE);
        }else {
            subjectContent = " %s" + String.format("(Total value is above %s RMB)", ManageConfig.GIVING_UNIT_MAX_PRICE);
        }
        return subjectContent;
    }


    private void fillInSubject(String groupFullName) {
        log.info("fill in subject...");
        String actionType = this.getActionType();
        log.info("actionType: {}", actionType);
        String subjectPreset = StringUtils.EMPTY;
        switch (actionType){
            case Constant.GIFT_SUBMIT_TYPE:
            case Constant.GIFTS_DOCUMENTED_TYPE:
                subjectPreset =  "Yes".equals(isGoSoc) ? getIsScoMark() : getPriceRangeMark();
                break;
            case Constant.GIFTS_APPROVE_TYPE:
            case Constant.GIFTS_REJECTED_TYPE:
                subjectPreset = "Yes".equals(isGoSoc) ? groupFullName +StringUtils.EMPTY + getIsScoMark() :
                        groupFullName +StringUtils.EMPTY + getPriceRangeMark();
                break;
            case Constant.GIFTS_CANCELLED_TYPE:
            case Constant.GIFTS_COPY_TYPE:
                break;
            default:
                break;
        }
        log.info("subject preset: {}", subjectPreset);
        this.setSubjectContent(StringUtils.isEmpty(subjectPreset) ? subjectPreset : "%s " + subjectPreset);
    }
}
