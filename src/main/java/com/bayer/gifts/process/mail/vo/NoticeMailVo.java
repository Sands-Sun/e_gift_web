package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.config.MailConfig;
import com.bayer.gifts.process.config.ManageConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Slf4j
public class NoticeMailVo extends BaseMailVo{

    private String appAddressUrl;
    private String taskId;
    private String executionId;
    private Long applicationId;

    public NoticeMailVo () {
        this.appAddressUrl = ManageConfig.APP_ADDRESS_URL;
    }

    protected void resetMailTo() {
        String mailTos = this.getMailTo();
        String mailCCs = this.getMailCc();
        log.info(">>>>>> mailTos: {}", mailTos);
        log.info(">>>>>> mailCCs: {}", mailCCs);
        if(MailConfig.MAIL_DEBUG_MAIL_SEND){
            log.info("debug mail send to...");
            this.setMailTo(MailConfig.MAIL_SEND_NOTIFY_TO);
            this.setMailCc(StringUtils.EMPTY);
            this.setMailRealTo(mailTos);
            this.setMailRealCc(mailCCs);
        }else {
            this.setMailTo(mailTos);
            this.setMailCc(mailCCs);
        }
    }
}
