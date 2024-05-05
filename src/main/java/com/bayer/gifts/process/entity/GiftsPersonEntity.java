package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Data
@TableName("B_MD_GIFT_PERSON_INFO")
public class GiftsPersonEntity extends GiftsBaseEntity  implements Serializable {

    private static final long serialVersionUID = -680627593587736360L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long companyId;
    @TableField(exist = false)
    private String companyName;
    private String personName;
    private String description;
    private String markDeleted;
    private String positionTitle;//POSITION_TITLE,职务 add by lining at 20151229
    private Long createdBy;
    private Long lastModifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof GiftsPersonEntity)) return false;

        GiftsPersonEntity that = (GiftsPersonEntity) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getCompanyName(), that.getCompanyName()).append(getPersonName(), that.getPersonName()).append(getPositionTitle(), that.getPositionTitle()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getCompanyName()).append(getPersonName()).append(getPositionTitle()).toHashCode();
    }
}
