package com.bayer.gifts.process.param;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class GiftsGroupParam extends PageParam implements Serializable {

//    @NotNull(message = "group ID不能为空", groups = {UpdateGroup.class})
    private Long id;
    @NotBlank(message = "group code不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String groupCode;
    @NotBlank(message = "group 名称不能为空", groups = {AddGroup.class,UpdateGroup.class})
    private String groupName;
    private String fullName;
    private String remark;
    private String markDeleted;

    private List<String> userEmails;
    private List<OrderByParam> orders;
}
