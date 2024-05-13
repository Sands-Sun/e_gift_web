package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GiftCompInfoForm {

    @NotNull(message = "公司/实体ID不能为空", groups = {AddGroup.class})
    private String companyName;


    private List<GiftsPersonEntity> personList;

}
