package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public class GivingGiftsProcessNoticeMailVo extends GiftsBaseNoticeMailVo{
    @MailContentFieldIgnore(value = true)
    private GivingGiftsApplyVariable applyVariable;

    private Integer volume;
    private Double unitValue;
    private Double totalValue;
    private String giftDescType;
    private String giftDesc;
    private String reasonType;
    //    private String signature;
    private String givenPersons;
    private String givenCompany;
    private String givenDate;
    //是否是政府官员或国有企业员工
    private String isGoSoc;
    //接受者是否是拜耳现有客户
    private String isBayerCustomer;

    private List<GiftsUserToGroupEntity> scoGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> departmentHeadGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> countryHeadGroupUserList = new ArrayList<>();

    private List<GiftsRelationPersonEntity> giftsPersonList = new ArrayList<>();

    public GivingGiftsProcessNoticeMailVo() {

    }

    public GivingGiftsProcessNoticeMailVo(GiftsApplyBaseVariable applyVariable) {
        super(Constant.GIFTS_TYPE,Constant.GIFTS_GIVING_TYPE);
        this.applyVariable = (GivingGiftsApplyVariable) applyVariable;
        copyProperties();
        Map<String,List<GiftsUserToGroupEntity>> hisProcessGroups = this.applyVariable.getHisProcessGroups();
        List<GiftsUserToGroupEntity> hisGroupUserList = hisProcessGroups
                .values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        List<String> mailToList =hisGroupUserList.stream().map(GiftsUserToGroupEntity::getUserEmail)
                .collect(Collectors.toList());
        List<String> mailCcList = applyVariable.getCopyToUserEmails();
        if(CollectionUtils.isNotEmpty(mailToList)){
            this.setMailTo(String.join(";", mailToList));
        }
        if(CollectionUtils.isNotEmpty(mailCcList)){
            this.setMailCc(String.join(";", mailCcList));
        }
        log.info("group: {}, mailToList: {}",hisProcessGroups.keySet(), mailToList);
        this.setMailSender(companyCode + "_" + this.getProcessType() + "_");
        this.resetMailTo();
    }

    public GivingGiftsProcessNoticeMailVo(GivingGiftsApplyVariable applyVariable,
                                          GiftsTaskVariable taskVariable,
                                          String executionId) {
//        this.setProcessType("Gift");
//        this.setMailType(Constant.GIFTS_GIVING_TYPE);
////        this.setActionType(applyVariable.getActionType());
        super(taskVariable,Constant.GIFTS_TYPE,Constant.GIFTS_GIVING_TYPE);
        this.applyVariable = applyVariable;
        copyProperties();
//        this.setAutoSent(false);
        this.setMailSender(companyCode + "_" + this.getProcessType() + "_");
        this.setExecutionId(executionId);

        this.fillExtraInfo();
        this.resetMailTo();
    }


    private void copyProperties() {
        BeanUtils.copyProperties(applyVariable,this,
                "scoGroupUserList","departmentHeadGroupUserList",
                "countryHeadGroupUserList", "giftsPersonList");
        this.totalValue = applyVariable.getTotalValue();
        this.scoGroupUserList = this.applyVariable.getScoGroupUserList();
        this.departmentHeadGroupUserList = this.applyVariable.getDepartmentHeadGroupUserList();
        this.countryHeadGroupUserList = this.applyVariable.getCountryHeadGroupUserList();
        this.giftsPersonList = this.applyVariable.getGiftsPersonList();
    }

    private void fillExtraInfo() {
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupPair = applyVariable.getCurrentGroupUserPair();
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupPair = applyVariable.getFromGroupUserPair();
        boolean emptyFromGroup = Objects.isNull(fromGroupPair);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> groupPair =
                Objects.isNull(fromGroupPair) ? currentGroupPair : fromGroupPair;
        log.info("from group is empty ?  {}",emptyFromGroup);
        String actionType = emptyFromGroup ? Constant.GIFT_SUBMIT_TYPE : this.getActionType();
        GiftsGroupEntity group = groupPair.getKey();
        String groupFullName = group.getFullName();
        this.fillInSubject(groupFullName,actionType);
        List<GiftsUserToGroupEntity> userToGroups = currentGroupPair.getValue();
        List<String> mailToList =userToGroups.stream().map(GiftsUserToGroupEntity::getUserEmail)
                .collect(Collectors.toList());
        if(Objects.nonNull(taskVariable)) {
            this.setActionType(emptyFromGroup ? Constant.GIFT_SUBMIT_TYPE : taskVariable.getApprove());
            this.setTaskId(taskVariable.getTaskId());
        }
        if(CollectionUtils.isNotEmpty(mailToList)){
           this.setMailTo(String.join(";", mailToList));
        }
        log.info("group: {}, mailToList: {}", groupFullName, mailToList);
    }





    //(是否是政府官员或国有企业员工)isGoSoc --> Yes Subject: recipient is gov. official or SOE employee.
    private String getIsScoMark() {
       return  " recipient is gov. official or SOE employee.";
    }

    private String getPriceRangeMark() {
        //1391 || 2614 below
        //Giving gifts request for your approval: (Total value is below 300 RMB)
        //Giving gifts request for your approval: (Total value is above 500 RMB)
        //Giving gifts request for your approval: (Total value is between 300 RMB and 500 RMB)
        String subjectContent;
        if(totalValue < ManageConfig.GIFT_UNIT_MIN_PRICE){
            subjectContent = String.format("(Total value is below %s RMB)",ManageConfig.GIFT_UNIT_MIN_PRICE);
        } else if(totalValue <= ManageConfig.GIFT_UNIT_MAX_PRICE){
            subjectContent =  String.format("(Total value is between %s RMB and %s RMB)",
                    ManageConfig.GIFT_UNIT_MIN_PRICE,ManageConfig.GIFT_UNIT_MAX_PRICE);
        }else {
            subjectContent = String.format("(Total value is above %s RMB)", ManageConfig.GIFT_UNIT_MAX_PRICE);
        }
        return subjectContent;
    }


    private void fillInSubject(String groupFullName, String actionType) {
        log.info("fill in subject...");
//        String actionType = this.getActionType();
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
//            case Constant.GIFTS_CANCELLED_TYPE:
//            case Constant.GIFTS_COPY_TYPE:
//                break;
            default:
                break;
        }
        log.info("subject preset: {}", subjectPreset);
        this.setSubjectContent(StringUtils.isEmpty(subjectPreset) ? subjectPreset : " %s " + subjectPreset);
    }
}
