package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.dao.GiftsFileDao;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonBaseEntity;
import com.bayer.gifts.process.service.GiftsCompanyService;
import com.bayer.gifts.process.service.StorageService;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.bayer.gifts.process.utils.DateUtils.RUNNINGTIME_PATTERN;

@Slf4j
@Service("storageService")
public class StorageServiceImpl extends ServiceImpl<GiftsFileDao, FileUploadEntity> implements StorageService {


    @Autowired
    GiftsCompanyService giftsCompanyService;

    @Override
    public void downloadFileTemplate(HttpServletResponse response, String module,String fileName) {
        File file = new File( ManageConfig.TEMPLATE_BASE_PATH + File.separator + module + File.separator + fileName );
        if(!file.exists()){
            return;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try(BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            log.error("download file error:",e);
        }
    }

    @Override
    public void updateFileMap(FileMapEntity fileMap) {
        this.baseMapper.updateFileMap(fileMap);
    }

    @Override
    public void saveFileMap(FileMapEntity fileMap) {
        this.baseMapper.insertFileMap(fileMap);
    }
    @Override
    public void deleteFileMapByAppId(Long applicationId,String type) {
        this.baseMapper.deleteFileMapByAppId(applicationId,type);
    }


    @Override
    public void saveFileAttach(Date currentDate,Long applicationId,Long userId,String type,List<Long> fileIds) {
        log.info("save file attachment...");
        if(CollectionUtils.isEmpty(fileIds)){
            return;
        }
        List<FileMapEntity> fileMaps = this.baseMapper.selectFileMapByAppId(type,applicationId);
        log.info("applicationId: {}, fileMap size: {}",applicationId, fileMaps.size());
        List<Long> excludeFileIds = fileMaps.stream()
                .map(FileMapEntity::getFileId)
                .filter(fileId -> !fileIds.contains(fileId)).collect(Collectors.toList());
        log.info("excludeFileIds: {}", excludeFileIds);
        if(CollectionUtils.isNotEmpty(excludeFileIds)){
            this.baseMapper.deleteFileMap(type,excludeFileIds);
        }
        fileIds.forEach(fileId-> saveFileAttach(currentDate,applicationId,userId,type,fileId));
    }

    @Override
    public void saveFileAttach(Date currentDate,Long applicationId, Long userId,String type, Long fileId) {
        if(Objects.isNull(fileId)){
            log.info("fileId is empty...");
            deleteFileMapByAppId(applicationId,type);
            return;
        }
        FileUploadEntity fileUpload = getById(fileId);
        fileUpload.setCreatedBy(String.valueOf(userId));
        fileUpload.setLastModifiedBy(String.valueOf(userId));
        fileUpload.setLastModifiedDate(currentDate);
        updateById(fileUpload);
        updateFileMap(currentDate,applicationId,userId,fileId);
    }
    @Override
    public void updateFileMap(Date currentDate,Long applicationId, Long userId, Long fileId) {
        log.info("update file map...");
        FileMapEntity fileMap = new FileMapEntity();
        fileMap.setApplicationId(applicationId);
        fileMap.setFileId(fileId);
        fileMap.setCreatedBy(String.valueOf(userId));
        fileMap.setCreatedDate(currentDate);
        fileMap.setLastModifiedBy(String.valueOf(userId));
        fileMap.setLastModifiedDate(currentDate);
        updateFileMap(fileMap);
    }




    @Override

    public FileUploadEntity copyDownloadFile(FileUploadEntity fileUpload) {
        FileMapEntity fileMap = fileUpload.getFileMap();
        FileUploadEntity copyFileUpload = null;
        String copyOrigFile = DateUtils.format(new Date(), RUNNINGTIME_PATTERN) + "_copy_" + fileUpload.getOrigFileName();
        Path sourcePath = Paths.get(ManageConfig.UPLOAD_FILE_PATH + File.separator + fileUpload.getFilePath());
        Path targetPath = Paths.get(ManageConfig.UPLOAD_FILE_PATH, fileMap.getModule() + "/",fileMap.getType() + "/" + copyOrigFile);
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            copyFileUpload = new FileUploadEntity();
            BeanUtils.copyProperties(fileUpload,copyFileUpload);
            this.baseMapper.insert(copyFileUpload);
        }
        catch (IOException e) {
            log.error("upload file error: ",e);
        }
        return copyFileUpload;
    }


    @Override
    public void downloadFile(HttpServletResponse response, Long fileId, String filePath) {
        if(Objects.nonNull(fileId)){
            log.info("download file by fileId: {}", fileId);
            downloadFileById(response,fileId);
        }else {
            log.info("download file by url: {}", filePath);
            downloadFileByUrl(response,filePath);
        }
    }

    private void downloadFileByUrl(HttpServletResponse response, String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
        downloadFile(response,filePath,fileName);
    }

    private void downloadFileById(HttpServletResponse response,Long fileId) {
        FileUploadEntity fileUpload = this.baseMapper.selectById(fileId);
        if(Objects.isNull(fileUpload)){
            return;
        }
        downloadFile(response, fileUpload.getFilePath(), fileUpload.getFileName());
    }


    private void downloadFile(HttpServletResponse response, String filePath, String fileName) {
        log.info("fileName: {}, filePath: {}", fileName,filePath);
        File file = new File(ManageConfig.UPLOAD_FILE_PATH + File.separator + filePath);
        if(!file.exists()){
            return;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try(BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            log.error("download file error:",e);
        }
    }
    @Override
    public FileUploadEntity uploadFile(MultipartFile multipartFile, String module,
                                              String type, String companyCode) {
        FileUploadEntity fileUpload = uploadFile(multipartFile, module, type);
        ///sys/upload/file?module=hospitality&type=CompanyPerson
        File file = new File(ManageConfig.UPLOAD_FILE_PATH + fileUpload.getFilePath());
        List<GiftsCompanyEntity> companyList = giftsCompanyService.mergeFromFileAttach(file,module,companyCode);
        log.info("gifts companyList size: {}", companyList.size());
        fileUpload.setExtData(companyList);
        return fileUpload;
    }


    @Override
    public FileUploadEntity uploadFile(MultipartFile multipartFile, String module, String type) {
        Path rootLocation = Paths.get(ManageConfig.UPLOAD_FILE_PATH, module + "/",type);
        FileUploadEntity fileUpload = null;
        Date currentDate = new Date();
        if(multipartFile.isEmpty()){
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        log.info("fileName: {}", originalFilename);
        // 设置文件类型
        log.info("contentType: {}", multipartFile.getContentType());
        // 设置文件大小
        log.info("fileSize {}", multipartFile.getSize());
        String fileName = DateUtils.format(new Date(), RUNNINGTIME_PATTERN) + "_" + originalFilename;
        Path destinationFile = rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();
//        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String relPath = destinationFile.toString().substring(ManageConfig.UPLOAD_FILE_PATH.length());
            Files.createDirectories(destinationFile.getParent());
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            fileUpload = saveUploadFile(fileName,originalFilename,relPath,currentDate,multipartFile);
            saveFileMap(fileUpload,currentDate,module,type);
        }
		catch (IOException e) {
            log.error("upload file error: ",e);
        }
        return fileUpload;
    }

    @Override
    public List<FileUploadEntity> uploadFiles(MultipartFile[] multipartFiles, String module, String type) {
        log.info("Multiple file upload...");
        if(multipartFiles.length == 0){
            return Collections.emptyList();
        }
        List<FileUploadEntity> fileUploads = new ArrayList<>(multipartFiles.length);
        for(MultipartFile multipartFile : multipartFiles){
            FileUploadEntity fileUpload = uploadFile(multipartFile, module, type);
            fileUploads.add(fileUpload);
        }
        return fileUploads;
    }

    @Override
    public FileUploadEntity getUploadFile(Long applicationId,String module,String type) {
        List<FileUploadEntity> fileUploads = getUploadFiles(applicationId,module,type);
        log.info("fileUploads size: {}", fileUploads.size());
        return fileUploads.stream().findFirst().orElse(null);
    }
    @Override
    public List<FileUploadEntity> getUploadFiles(Long applicationId, String module, String type) {
        return this.baseMapper.selectUploadFile(applicationId,module,type);
    }


    private void saveFileMap (FileUploadEntity fileUpload,Date currentDate,String module, String type) {
        FileMapEntity fileMap = new FileMapEntity();
        fileMap.setApplicationId(9999L);
        fileMap.setFileId(fileUpload.getId());
        fileMap.setModule(module);
        fileMap.setType(type);
//        fileMap.setCreatedBy(String.valueOf(user.getSfUserId()));
//        fileMap.setLastModifiedBy(String.valueOf(user.getSfUserId()));
        fileMap.setCreatedDate(currentDate);
        fileMap.setLastModifiedDate(currentDate);
        this.baseMapper.insertFileMap(fileMap);
    }

    private FileUploadEntity saveUploadFile(String fileName, String originalFilename, String filePath, Date currentDate,
                                            MultipartFile multipartFile) {
        FileUploadEntity fileUpload = new FileUploadEntity();
        fileUpload.setFilePath(filePath);
        fileUpload.setFileName(fileName);
        fileUpload.setOrigFileName(originalFilename);
        fileUpload.setFileType(multipartFile.getContentType());
        fileUpload.setFileSize(String.valueOf(multipartFile.getSize()));
//        fileUpload.setCreatedBy(String.valueOf(user.getSfUserId()));
//        fileUpload.setLastModifiedBy(String.valueOf(user.getSfUserId()));
        fileUpload.setCreatedDate(currentDate);
        fileUpload.setLastModifiedDate(currentDate);
        this.baseMapper.insert(fileUpload);
        return fileUpload;
    }

}
