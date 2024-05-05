package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface StorageService extends IService<FileUploadEntity> {

    FileUploadEntity uploadFile(MultipartFile multipartFile, String module, String type);
    void downloadFile(HttpServletResponse response, Long fileId);
    void downloadFileTemplate(HttpServletResponse response, String module,String fileName);
    FileUploadEntity getUploadFile(Long applicationId,String module,String type);
    void updateFileMap(FileMapEntity fileMap);
}
