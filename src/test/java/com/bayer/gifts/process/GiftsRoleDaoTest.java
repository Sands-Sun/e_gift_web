package com.bayer.gifts.process;

import com.bayer.gifts.activiti.extension.GiftsNotifMailDelegate;
import com.bayer.gifts.process.dao.GiftsRoleDao;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.entity.GiftsRoleEntity;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.utils.MailUtils;
import com.bayer.gifts.process.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsRoleDaoTest {

    @Autowired
    GiftsRoleDao giftsRoleDao;

    @Test
    public void testQueryRoleListByUserId() {
        List<GiftsRoleEntity> groups = giftsRoleDao.queryRoleListByUserId(2975L);
        for(GiftsRoleEntity giftsRole : groups){
            log.info("role name: {}", giftsRole.getRoleName());
        }
    }
}
