package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GiftsFormBase extends FormBase{

    @NotNull(message = "单价不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Double unitValue;
    @NotNull(message = "数量不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer volume;
}
