package com.bayer.gifts.process;

import com.bayer.gifts.process.dao.GiftsFileDao;
import com.bayer.gifts.process.service.StorageService;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsFileDaoTest {

    @Autowired
    StorageService storageService;

    @Autowired
    GiftsFileDao giftsFileDao;


    @Test
    public void testSelectUploadFile() {
        List<FileUploadEntity> fileUpload =
                giftsFileDao.selectUploadFile(2263L, "receiving","CompanyPerson");
        System.out.println(fileUpload);
    }
}
