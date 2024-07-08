package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GiftsFileDao extends BaseMapper<FileUploadEntity> {

    void insertFileMap(FileMapEntity fileMap);
    void updateFileMap(FileMapEntity fileMap);
    void deleteFileMap(Collection<Long> fileIds);
    void deleteFileMapByAppId(Long applicationId);
    FileMapEntity selectFileMap(Long fileId);
    List<FileMapEntity> selectFileMapByAppId(Long applicationId);

    List<FileUploadEntity> selectUploadFile(@Param("applicationId") Long applicationId,
                                            @Param("module") String module,
                                            @Param("type") String type);
}
