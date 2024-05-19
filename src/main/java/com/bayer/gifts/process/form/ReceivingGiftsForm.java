package com.bayer.gifts.process.form;

import lombok.Data;

import java.util.List;

@Data
public class ReceivingGiftsForm extends GiftsFormBase {

    private String givingTitle;

    private Double estimatedTotalValue;

    private String isSco;

    // 礼品已上交SCO
    private String isHandedOver;
    // 是否属于列出的情况
    private String isInvolved;
    // 是否不包括列出的情况
    private String isExcluded;

    private String useCase;

    private List<GiftCompInfoForm> companyList;
}
