package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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

    private List<CompanyInfoForm> companyList;
}
