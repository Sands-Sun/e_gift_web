package com.bayer.gifts.process;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.dao.GiftsProcessDao;
import com.bayer.gifts.process.mail.vo.GivingGiftsProcessNoticeMailVo;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import com.bayer.gifts.process.vo.TaskInstanceVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsProcessDaoTest {

    @Autowired
    private GiftsProcessDao processDao;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;


    @Autowired
    RepositoryService  repositoryService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    IdentityService identityService;

    @Test
    public void testTaskList() {
//        GiftsTaskParam param = new GiftsTaskParam();
//        param.setUserId(3471L);
//        IPage<TaskInstanceVo> list = processDao.queryTaskList(new Page<>(1,2),param);
//        System.out.println(list);

        List<Task> tasks = taskService.createTaskQuery().
                processInstanceId("255001").list();


        for(Task task : tasks){
            System.out.println(task);
            Map<String, Object> var = task.getProcessVariables();
            log.info("Assignee: {}",task.getAssignee());
            log.info("TaskId: {}",task.getId());
            log.info("ExecutionId: {}",task.getExecutionId());
            log.info("ParentTaskId: {}",task.getParentTaskId());
            log.info("Variables: {}", var);

            List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
            for(IdentityLink identityLink : identityLinkList){
                log.info("identityLink: {}", identityLink);
            }
        }
    }

    @Test
    public void testTaskAssignee() {
//        List<Task> tasks = taskService.createTaskQuery().taskAssignee("41811").list();
//        taskService.createTaskQuery().taskCandidateGroup("");
    }
//    @Test
    public void testGroup() {
//        Group group = identityService.newGroup("user");
//        group.setName("users");
//        group.setType("security-role");
//        identityService.saveGroup(group);
//
//        User admin = identityService.newUser("admin");
//        admin.setPassword("admin");
//        identityService.saveUser(admin);
    }


    @Test
    public void testHistoryTask() {
      List<HistoricTaskInstance> list =
              historyService.createHistoricTaskInstanceQuery().taskCandidateUser("41811").list();
      for(HistoricTaskInstance history: list){
            System.out.println(history);
      }
    }

    @Test
    public void testRepService() {
//        repositoryService.ge
//        repositoryService.deleteDeployment("232501",true);

        Map<String, Object> variables = runtimeService.getVariables("255001");
        GivingGiftsApplyVariable applyVariable =
                (GivingGiftsApplyVariable)variables.get("applyGivingGiftsVar");
        GiftsTaskVariable taskVariable = (GiftsTaskVariable)variables.get("taskVariable");
        new GivingGiftsProcessNoticeMailVo(applyVariable,taskVariable,null);



        //255001
        List<HistoricActivityInstance> historicActivityInstanceList =
                historyService.createHistoricActivityInstanceQuery()
                .processInstanceId("255001")
                .activityType("userTask")   //用户任务
                .finished()       //已经执行的任务节点
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();
        for(HistoricActivityInstance activityInstance : historicActivityInstanceList){
            System.out.println(activityInstance);
        }

    }
}
