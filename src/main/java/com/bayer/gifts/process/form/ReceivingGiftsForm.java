package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReceivingGiftsForm extends GiftsFormBase{

    @NotBlank(message = "接收人不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String givingPerson;

    @NotBlank(message = "公司/实体不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String givingCompany;

    private String givingTitle;

    private String isSco;

    // 礼品已上交SCO
    private String isHandedOver;
    // 是否属于列出的情况
    private String isInvolved;
    // 是否不包括列出的情况
    private String isExcluded;
}
