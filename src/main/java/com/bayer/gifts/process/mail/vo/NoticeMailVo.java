package com.bayer.gifts.process.mail.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeMailVo extends BaseMailVo{

    private String taskId;
    private String executionId;
}
