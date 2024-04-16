package com.bayer.gifts.process;

import com.bayer.gifts.activiti.listener.GiftsTaskListener;
import com.bayer.gifts.process.controller.GivingGiftsController;
import com.bayer.gifts.process.controller.LoginController;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.service.GivingGiftsService;
import com.bayer.gifts.process.service.ProcessService;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.utils.DateUtils;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.junit.Rule;
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
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GivingGiftsBizGroupBHCTest {


    @Autowired
    GivingGiftsService givingGiftsService;


    @Autowired
    TaskService taskService;


    @Autowired
    GiftsTaskListener giftsTaskListener;

    @Autowired
    ProcessService processService;


//    @Test
//    public void clearAllTables() {
//        commandExecutor.execute(new Command<Void>() {
//            public Void execute(CommandContext commandContext) {
//                DbSqlSession session = commandContext.getDbSqlSession();
//                session.dbSchemaDrop();
//                session.dbSchemaCreate();
//                return null;
//            }
//        });
//    }


    @Test
    public void handleTask() {
        //30001  30010
        GiftsTaskFrom taskFrom = new GiftsTaskFrom();
        taskFrom.setUserId(3221L);
        taskFrom.setApplicationId(4221L);
        taskFrom.setTaskId("97510");
        taskFrom.setApprove("Rejected");
        taskFrom.setComment("Rejected your request for giving gifts !!!");
        taskFrom.setProcessType("Gifts");
        processService.handleTask(taskFrom);
    }
    @Test
    public void testTask() {
        //3221, 3471, 4911
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("3221").list();
        for(Task task : tasks){
            log.info("ProcessInstanceId: {}", task.getProcessInstanceId());
            log.info("ProcessDefinitionId: {}",task.getProcessDefinitionId());
            log.info("Assignee: {}",task.getAssignee());
            log.info("TaskId: {}",task.getId());
            log.info("ExecutionId: {}",task.getExecutionId());
            log.info("ParentTaskId: {}",task.getParentTaskId());
            Map<String, Object> var = task.getProcessVariables();
            log.info("Variables: {}", var);

        }
        //160001
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
//        form.setCopyToUserIds(Arrays.asList(3037L,3027L));
        form.setActionType("Submit");
        form.setReason("Mid-autum day_Mooncake");
        form.setUnitValue(123D);
        form.setVolume(5);
        form.setReasonType("Giving Gifts");
        form.setGivenPersons(Arrays.asList("李辉","丁大刚","林国宏","李振国"));
        form.setGivenCompany("平安种业");
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
