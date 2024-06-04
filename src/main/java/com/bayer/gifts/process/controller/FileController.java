package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "sys")
@Api(tags = "文件管理")
public class FileController {

    @Autowired
    StorageService storageService;

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/file", method = RequestMethod.POST)
    public R fileUpload(@RequestParam("file") MultipartFile multipartFile,
                              @RequestParam("module") String module,
                              @RequestParam("type") String type) {
        return R.ok(storageService.uploadFile(multipartFile,module,type));
    }

    @ApiOperation("下载文件")
    @RequestMapping(value = "/download/file", method = RequestMethod.GET)
    public void fileDownload(HttpServletResponse response, @RequestParam("fileId") Long fileId){
        storageService.downloadFile(response,fileId);
    }

    @ApiOperation("下载文件")
    @RequestMapping(value = "/download/template", method = RequestMethod.GET)
    public void downloadFileTemplate(HttpServletResponse response,
                                  @RequestParam("module") String module,
                                  @RequestParam("fileName") String fileName) {
        storageService.downloadFileTemplate(response,module,fileName);
    }
}
