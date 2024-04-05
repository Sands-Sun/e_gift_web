package com.bayer.gifts.process;

import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import java.io.IOException;
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsMailTest {

    @Autowired
    BatchCompleteMailService batchCompleteMailService;



    @Test
    public void sendMailTest() {
        BatchCompleteMail completeMail= batchCompleteMailService.getById(21);
        try {
            MailUtils.sendMail(completeMail.getMailTo(),
                    completeMail.getMailSubject(),completeMail.getMailBody(), null);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
