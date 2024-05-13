package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import com.bayer.gifts.process.variables.ReceivingGiftsApplyVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public class ReceivingGiftsProcessNoticeMailVo extends GiftsBaseNoticeMailVo{

    @MailContentFieldIgnore(value = true)
    private ReceivingGiftsApplyVariable applyVariable;

    private Date givingDate;
    private Double unitValue;
    private Integer volume;
    private Double estimatedTotalValue;
    private String reasonType;
    private String reason;
    private String giftDescType;
    private String giftDesc;
    // 礼品已上交SCO
    private String isHandedOver;
    // 是否属于列出的情况
    private String isInvolved;
    // 是否不包括列出的情况
    private String isExcluded;

    private List<GiftsRelationPersonEntity> giftsPersonList = new ArrayList<>();

    public ReceivingGiftsProcessNoticeMailVo() {

    }

    public ReceivingGiftsProcessNoticeMailVo(GiftsApplyBaseVariable applyVariable) {
        super(Constant.GIFTS_TYPE,Constant.GIFTS_RECEIVING_TYPE);
        this.applyVariable = (ReceivingGiftsApplyVariable) applyVariable;
        copyProperties();
        List<String> mailCcList = applyVariable.getCopyToUserEmails();
        if(StringUtils.isNotEmpty(applyVariable.getApplyEmail())){
            this.setMailTo(applyVariable.getApplyEmail());
        }
        if(CollectionUtils.isNotEmpty(mailCcList)){
            this.setMailCc(String.join(";", mailCcList));
        }
        log.info("mailTo: {}, mailCcList: {}",applyVariable.getApplyEmail(), mailCcList);
        this.setMailSender(companyCode + "_" + this.getProcessType() + "_");
        this.resetMailTo();
    }


    private void copyProperties() {
        BeanUtils.copyProperties(applyVariable,this,
                "scoGroupUserList","departmentHeadGroupUserList",
                "countryHeadGroupUserList", "giftsPersonList");
        this.estimatedTotalValue = applyVariable.getEstimatedTotalValue();
        this.giftsPersonList = this.applyVariable.getGiftsPersonList();
    }
}
