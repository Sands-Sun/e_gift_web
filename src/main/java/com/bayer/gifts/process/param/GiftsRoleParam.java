package com.bayer.gifts.process.param;

import com.bayer.gifts.process.common.param.PageParam;
import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class GiftsRoleParam extends PageParam implements Serializable {

    private Long id;
    @NotBlank(message = "role不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String roleName;
    @NotBlank(message = "role code名称不能为空", groups = {AddGroup.class,UpdateGroup.class})
    private String roleCode;
    private String remark;
    private String markDeleted;
}
