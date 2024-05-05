package com.bayer.gifts.process.sys.entity;


import com.bayer.gifts.process.entity.GiftsBaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileMapEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = -4312100265762827445L;
    private Long id;
    private Long applicationId;
    private Long fileId;
    private String module;
    private String type;
    private String createdBy;
    private String lastModifiedBy;
}
