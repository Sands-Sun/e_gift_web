package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface StorageService extends IService<FileUploadEntity> {

    FileUploadEntity copyDownloadFile(FileUploadEntity fileUpload);
    FileUploadEntity uploadFile(MultipartFile file, String module, String type);
    List<FileUploadEntity> uploadFiles(MultipartFile[] files, String module, String type);
    FileUploadEntity uploadFile(MultipartFile file, String module, String type, String companyCode);
    void downloadFile(HttpServletResponse response, Long fileId, String filePath);
    void downloadFileTemplate(HttpServletResponse response, String module,String fileName);

    List<FileUploadEntity> getUploadFiles(Long applicationId, String module, String type);
    FileUploadEntity getUploadFile(Long applicationId,String module,String type);
    void updateFileMap(FileMapEntity fileMap);

    void saveFileMap(FileMapEntity fileMap);

    void deleteFileMapByAppId(Long applicationId,String type);

    void saveFileAttach(Date currentDate, Long applicationId, Long userId,String type, Long fileId);
    void saveFileAttach(Date currentDate,Long applicationId, Long userId,String type,List<Long> fileIds);

    void updateFileMap(Date currentDate,Long applicationId, Long userId, Long fileId);
}
