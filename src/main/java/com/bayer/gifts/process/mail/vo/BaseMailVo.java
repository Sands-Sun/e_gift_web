package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.common.MailContentFieldIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseMailVo {


    @MailContentFieldIgnore(value = true)
    private String mailSender;

    @MailContentFieldIgnore(value = true)
    private String subjectContent;

    @MailContentFieldIgnore(value = true)
    private String actionType;

    @MailContentFieldIgnore(value = true)
    private String attachment;

    @MailContentFieldIgnore(value = true)
    private String processType;
    @MailContentFieldIgnore(value = true)
    private String mailType;

    @MailContentFieldIgnore(value = true)
    private String mailTo;

    @MailContentFieldIgnore(value = true)
    private Boolean autoSent;

    private String errorLog;

    public BaseMailVo() {
    }

    public BaseMailVo(String mailType) {
        this.mailType = mailType;
    }
}
