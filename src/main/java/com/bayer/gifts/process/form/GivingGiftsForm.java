package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class GivingGiftsForm extends GiftsFormBase{

    @NotNull(message = "接收人ID不能为空", groups = {AddGroup.class})
    private List<Long> givenPersons;

    @NotNull(message = "公司/实体ID不能为空", groups = {AddGroup.class})
    private Long givenCompanyId;

    //是否是政府官员或国有企业员工
    @Pattern(regexp = "Yes|No", groups = {AddGroup.class, UpdateGroup.class})
    private String isGoSoc;

    //接受者是否是拜耳现有客户
    @Pattern(regexp = "Yes|No", groups = {AddGroup.class, UpdateGroup.class})
    private String isBayerCustomer;
}
