package com.bayer.gifts.process;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.FileNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ProcessDeploy {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void deployProcessDefinition() throws FileNotFoundException {
        Deployment deploy = this.repositoryService.createDeployment()
//                .addClasspathResource("./processes/Developer_Hiring.bpmn")
//                .addClasspathResource("./processes/Giving_Gifts_1391.bpmn")
//                .addClasspathResource("processes/Vacation_request.bpmn")
                .addClasspathResource("./processes/Giving_Gifts_0882.bpmn")
                .deploy();
        System.out.println("部署流程定义成功："+ deploy);
    }
}
