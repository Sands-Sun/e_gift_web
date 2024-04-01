package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_MD_DROP_DOWN")
public class GriftDropDownEntity extends GiftsBaseEntity  implements Serializable {

    private static final long serialVersionUID = 3666793281706227970L;
    @TableId(type = IdType.AUTO)
    private Integer dropDownId;
    private String dropDownKey;
    private String dropDownName;
    private String dropDownTxt;
    private String dropDownValue;
    private Integer sfOrgId;
    private Integer sfProcessId;
    private Integer dropDownIndex;
    private String remark;
    private String markDeleted;
}
