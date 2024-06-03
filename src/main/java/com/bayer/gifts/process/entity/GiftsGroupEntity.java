package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;
import org.activiti.engine.identity.Group;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("B_MD_GIFT_GROUP")
public class GiftsGroupEntity extends BaseEntity implements Group,Serializable {

    private static final long serialVersionUID = 8058923085772243677L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String groupName;
    private String fullName;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
    private String groupCode;

    @TableField(exist = false)
    private List<GiftsUserToGroupEntity> userToGroups;


    @Override
    public String getName() {
        return this.groupName;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getType() {
      return this.groupCode;
    }

    @Override
    public void setType(String s) {

    }
}
