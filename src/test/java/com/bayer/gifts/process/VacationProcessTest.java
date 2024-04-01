package com.bayer.gifts.process;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class VacationProcessTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentityService identityService;


    @Test
    public void testHistoryProcess() {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().finished().list();
        for(HistoricTaskInstance task : tasks){
            System.out.println("Task available: " + task.getName());
        }
//        taskService.createTaskQuery().taskAssignee()
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for(Task task : taskList){
            System.out.println("Task list: " + task.getName() + "Task id: " + task.getId());
        }
    }

    @Test
    public void testVacationProcess() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employeeName", "Kermit");
        variables.put("numberOfDays", new Integer(4));
        variables.put("vacationMotivation", "I'm really tired!");
        variables.put("useID", "123456");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationRequest", variables);
        // Verify that we started a new process instance
        System.out.println("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
//        identityService.createUserQuery()

//        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("123456").list();
        for (Task task : tasks) {
            System.out.println("Task available: " + task.getName());
        }

//        taskService.createTaskQuery().taskCandidateOrAssigned()
//
//        Task task = tasks.get(0);
//        Map<String, Object> taskVariables = new HashMap<String, Object>();
//        taskVariables.put("vacationApproved", "false");
//        taskVariables.put("managerMotivation", "We have a tight deadline!");
//        taskService.complete(task.getId(), taskVariables);

    }
}
