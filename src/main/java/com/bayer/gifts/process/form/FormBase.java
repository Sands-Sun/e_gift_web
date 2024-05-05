package com.bayer.gifts.process.form;

import com.alibaba.fastjson.annotation.JSONField;
import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
public class FormBase {

//    @NotNull(message = "application ID不能为空", groups = {AddGroup.class})
    private Long applicationId;

    private Long userId;

    @Pattern(regexp = "Draft|Submit|Cancel", message = "申请类别只能为(Draft|Submit|Cancel)",
            groups = {AddGroup.class, UpdateGroup.class})
    private String actionType;
    private Long applyForId;
    private Long fileId;
    private List<String> copyToUserEmails;
//    @NotBlank(message = "描述类别不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String giftDescType;
//    @NotBlank(message = "描述原因不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String giftDesc;
    //礼品描述
//    @NotBlank(message = "礼品描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String reason;
//    @NotBlank(message = "礼品描述原因不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String reasonType;

    @NotNull(message = "日期不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @JSONField(format = "yyyy-MM-dd")
    private Date date;
    private String remark;
}
