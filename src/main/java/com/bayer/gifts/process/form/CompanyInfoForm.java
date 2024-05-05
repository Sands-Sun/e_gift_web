package com.bayer.gifts.process.form;

import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompanyInfoForm {

    @NotNull(message = "公司/实体ID不能为空", groups = {AddGroup.class})
    private String companyName;

//    @NotNull(message = "接收人ID不能为空", groups = {AddGroup.class})
//    private String person;
//
//    private String title;

    private List<GiftsPersonEntity> persons;

}
