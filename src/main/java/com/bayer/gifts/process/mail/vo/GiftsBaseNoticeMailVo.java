package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;

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

}
