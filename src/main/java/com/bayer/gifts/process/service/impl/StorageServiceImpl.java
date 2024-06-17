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
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public void deleteFileMap(Long applicationId) {
        this.baseMapper.deleteFileMap(applicationId);
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
    public void downloadFile(HttpServletResponse response, Long fileId) {
        FileUploadEntity fileUpload = this.baseMapper.selectById(fileId);
        if(Objects.isNull(fileUpload)){
            return;
        }
        File file = new File(ManageConfig.UPLOAD_FILE_PATH + File.separator + fileUpload.getFilePath());
        if(!file.exists()){
            return;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileUpload.getFileName());
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
    public FileUploadEntity getUploadFile(Long applicationId,String module,String type) {
        List<FileUploadEntity> fileUploads = this.baseMapper.selectUploadFile(applicationId,module,type);
        log.info("fileUploads size: {}", fileUploads.size());
        return fileUploads.stream().findFirst().orElse(null);
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
