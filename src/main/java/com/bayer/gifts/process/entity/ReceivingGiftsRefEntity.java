package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_PROC_RECEIVING_GIFTS_REF")
public class ReceivingGiftsRefEntity extends GiftsBaseEntity  implements Serializable {

    private static final long serialVersionUID = -1228697514909183956L;
    @TableId(type = IdType.AUTO)
    private Long refId;
    private Long applicationId;
    private String giftDesc;
    private String giftDescType;
    private Double unitValue;
    private Integer volume;
    private Date givingDate;

    private String givingPerson;
    private String givingCompany;
    private String persionId;
    private String isSco;
    private String givingTitle;
}
