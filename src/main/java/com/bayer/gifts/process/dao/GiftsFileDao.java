package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GiftsFileDao extends BaseMapper<FileUploadEntity> {

    void insertFileMap(FileMapEntity fileMap);
    void updateFileMap(FileMapEntity fileMap);

    FileMapEntity selectFileMap(Long fileId);

    FileUploadEntity selectUploadFile(@Param("applicationId") Long applicationId,
                                    @Param("module") String module,
                                    @Param("type") String type);
}
