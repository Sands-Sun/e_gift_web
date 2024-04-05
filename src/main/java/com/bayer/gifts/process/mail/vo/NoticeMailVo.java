package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.config.ManageConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeMailVo extends BaseMailVo{

    private String appAddressUrl;
    private String taskId;
    private String executionId;
    private Long applicationId;

    public NoticeMailVo () {
        this.appAddressUrl = ManageConfig.APP_ADDRESS_URL;
    }
}
