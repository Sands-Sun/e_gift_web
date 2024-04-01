package com.bayer.gifts.process;

import com.bayer.gifts.process.utils.MailUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class GivingGiftsProcess {

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void testGivingGiftsProcess() throws MessagingException, IOException {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("email", "zhe.sun.ext@bayer.com");
        variables.put("user","Zhang San");
        variables.put("unitPrice", 100);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("givingGifts_1391", variables);
        System.out.println(processInstance);

        System.out.println(processInstance.getId());

//        MailUtils.sendMail("zhe.sun.ext@bayer.com","test mail","test content",null);

    }
}
