package com.bayer.gifts.process.variables;

import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Data
@Slf4j
public class ReceivingGiftsApplyVariable extends GiftsApplyBaseVariable{

    private static final long serialVersionUID = -3318099173200631535L;

    private String givingDate;
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

    private List<GiftsRelationPersonEntity> giftsPersonList;
}
