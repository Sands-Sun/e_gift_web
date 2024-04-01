package com.bayer.gifts.activiti.extension;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

@Slf4j
public class HelloWorldJavaDelegate implements JavaDelegate {


    @Override
    public void execute(DelegateExecution execution) {
        log.info("[Process=" + execution.getProcessInstanceId() +
                "][Java Delegate=" + this + "]");
        String employeeName = (String)execution.getVariable("employeeName");
        Integer numberOfDays = (Integer)execution.getVariable("numberOfDays");
        String vacationMotivation = (String)execution.getVariable("vacationMotivation");
        String useID = (String)execution.getVariable("useID");

        log.info("employeeName = " + employeeName);
        log.info("numberOfDays = " + numberOfDays);
        log.info("vacationMotivation = " + vacationMotivation);
        log.info("useID = " + useID);
        log.info("Execution id: " + execution.getId());
    }
}
