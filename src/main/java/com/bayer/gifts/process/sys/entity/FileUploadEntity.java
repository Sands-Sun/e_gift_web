package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("B_MD_GIFT_FILE_ATTACHEMENT")
public class FileUploadEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5598330790154691021L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String origFileName;
    private String fileName;
    private String fileSize;
    private String fileType;
    private String filePath;
    private String createdBy;
    private String lastModifiedBy;
    @TableField(exist = false)
    private FileMapEntity fileMap;
    @TableField(exist = false)
    private List extData;
}
