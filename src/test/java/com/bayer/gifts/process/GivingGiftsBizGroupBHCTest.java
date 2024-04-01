package com.bayer.gifts.process;

import com.bayer.gifts.process.controller.GivingGiftsController;
import com.bayer.gifts.process.controller.LoginController;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.service.GivingGiftsService;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GivingGiftsBizGroupBHCTest {


    @Autowired
    GivingGiftsService givingGiftsService;


    @Autowired
    TaskService taskService;

    @Test
    public void testTask() {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId("40001").list();

        for(Task task : tasks){
            System.out.println(task);
        }

    }



    @Test
    public void testGivingGiftsProcess() {
//        {
//            "actionType":"Draft",
//                "copyToUserIds":[
//            3037,
//                    3027
//   ],
//            "reason":"Mid-autum day_Mooncake",
//                "reasonType":"Giving Gifts",
//                "unitValue":123,
//                "volume":10,
//                "givenPersons":[
//            1,2,3,4,5
//   ],
//            "givenCompanyId":9037,
//                "giftDescType":"Company Branded Gift",
//                "giftDesc":"Special food for the Spring Fetival",
//                "isGoSoc":"Yes",
//                "isBayerCustomer":"No",
//                "remark":"Authority project related. Since attachment cannot be put onto e-gift system, hard copy will be provided.",
//                "date": "2023-12-12"
//        }

        GivingGiftsForm form = new GivingGiftsForm();
        form.setUserId(6951L);
        form.setApplicationId(4134L);
        form.setCopyToUserIds(Arrays.asList(3037L,3027L));
        form.setActionType("Submit");
        form.setReason("Mid-autum day_Mooncake");
        form.setUnitValue(123D);
        form.setVolume(5);
        form.setReasonType("Giving Gifts");
        form.setGivenPersons(Arrays.asList(1L,2L,3L,4L,5L));
        form.setGivenCompanyId(9037L);
        form.setGiftDesc("Special food for the Spring Fetival");
//        form.setGiftDescType("Other");
        form.setGiftDescType("Cultural Courtesy Gifts");
        form.setIsGoSoc("Yes");
        form.setIsBayerCustomer("No");
        form.setRemark("Authority project related. Since attachment cannot be put onto e-gift system, hard copy will be provided.");
        form.setDate(DateUtils.strToDate("2024-03-01"));
//        UserExtensionEntity user = shiroService.queryUser(2975L);
//        SecurityUtils.getSubject().getPrincipals();
        givingGiftsService.saveGivingGifts(form);

    }
}
