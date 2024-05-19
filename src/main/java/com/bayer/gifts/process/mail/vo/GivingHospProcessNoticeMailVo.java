package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.entity.HospitalityRelationPersonEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingHospApplyVariable;
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
public class GivingHospProcessNoticeMailVo extends GiftsBaseNoticeMailVo{

    @MailContentFieldIgnore(value = true)
    private GivingHospApplyVariable applyVariable;

    private String hospitalityDate;
    //请描述招待活动
    private String hospitalityType;
    //估计的人均费用
    private Double expensePerHead;
    //受邀人人数
    private Integer headCount;
    // 估计的总费用
    private Double estimatedTotalExpense;
    //地点
    private String hospPlace;
    private String reason;
    private String reasonType;

    //是否是政府官员或国有企业员工
    private String isGoSoc;
    private List<HospitalityRelationPersonEntity> hospPersonList;
    private List<GiftsUserToGroupEntity> scoGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> departmentHeadGroupUserList = new ArrayList<>();
    private List<GiftsUserToGroupEntity> countryHeadGroupUserList = new ArrayList<>();

    public GivingHospProcessNoticeMailVo() {

    }

    public GivingHospProcessNoticeMailVo(GiftsApplyBaseVariable applyVariable) {
        super(Constant.GIFTS_TYPE,Constant.HOSPITALITY_TYPE);
        this.applyVariable = (GivingHospApplyVariable) applyVariable;
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

    public GivingHospProcessNoticeMailVo(GivingHospApplyVariable applyVariable,
                                          GiftsTaskVariable taskVariable,
                                          String executionId) {
        super(taskVariable,Constant.HOSPITALITY_TYPE,Constant.GIFTS_GIVING_TYPE);
        this.applyVariable = applyVariable;
        copyProperties();
        this.setMailSender(companyCode + "_" + this.getProcessType() + "_");
        this.setExecutionId(executionId);
        this.fillExtraInfo();
        this.resetMailTo();
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

    private void fillInSubject(String groupFullName, String actionType) {
        log.info("fill in subject...");
//        String actionType = this.getActionType();
        log.info("actionType: {}", actionType);
        String subjectPreset = StringUtils.EMPTY;
        switch (actionType){
            case Constant.GIFT_SUBMIT_TYPE:
            case Constant.GIFTS_DOCUMENTED_TYPE:
                //TODO  subject preset
                break;
            case Constant.GIFTS_APPROVE_TYPE:
            case Constant.GIFTS_REJECTED_TYPE:
                //TODO  subject preset
                subjectPreset = groupFullName;
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

    private void copyProperties() {
        BeanUtils.copyProperties(applyVariable,this,
                "scoGroupUserList","departmentHeadGroupUserList",
                "countryHeadGroupUserList", "hospPersonList");
        this.estimatedTotalExpense = applyVariable.getEstimatedTotalExpense();
        this.scoGroupUserList = this.applyVariable.getScoGroupUserList();
        this.departmentHeadGroupUserList = this.applyVariable.getDepartmentHeadGroupUserList();
        this.countryHeadGroupUserList = this.applyVariable.getCountryHeadGroupUserList();
        this.hospPersonList = this.applyVariable.getHospPersonList();
    }
}
