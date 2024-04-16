package com.bayer.gifts.process.mail.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Slf4j
public class ReceivingGiftsDocumentMailVo extends NoticeMailVo{

    private String applyForName;
    private String creatorName;
    private String applyEmail;
    private String applyDate;
    private Long supervisorId;
    private String supervisorName;
    private String supervisorMail;
    private Integer volume;
    private Double unitValue;
    private Double totalValue;
    private String reason;
    private String referenceNo;
    private List<String> signatureList;
    private List<String> remarkList;
}
