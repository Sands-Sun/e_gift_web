package com.bayer.gifts.process.sys.param;

import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.common.param.PageParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class RoleParam extends PageParam implements Serializable {

    private static final long serialVersionUID = 1402479570773331179L;

    private Long id;
    private String userId;
    private String roleName;
    private String remark;
    private String status;
    private String markDeleted;
    private String functions;
    private List<String> userEmails;
    private List<OrderByParam> orders;
}
