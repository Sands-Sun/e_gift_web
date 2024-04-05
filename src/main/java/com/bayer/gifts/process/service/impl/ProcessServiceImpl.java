package com.bayer.gifts.process.service.impl;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GivingGiftsActivityDao;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.service.GivingGiftsService;
import com.bayer.gifts.process.service.ProcessService;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service("processService")
public class ProcessServiceImpl implements ProcessService {


    @Autowired
    ShiroService shiroService;

    @Autowired
    TaskService taskService;

    @Autowired
    GivingGiftsActivityDao givingGiftsActivityDao;


    @Override
    public void handleTask(GiftsTaskFrom form) {
//        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        //mock begin >>>>>
        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
        //mock end >>>>>
        String taskId = form.getTaskId();
        String processType  = form.getProcessType();
        Long applicationId = form.getApplicationId();
        Long userId = user.getSfUserId();
        log.info("handle task: {}, process type: {}", taskId, processType);
        Task task = taskService.createTaskQuery().taskId(form.getTaskId()).singleResult();
        if(Objects.isNull(task)){
            log.info("not found task...");
            return;
        }
        Map<String, Object> variables = Maps.newHashMap();
        GiftsTaskVariable taskVariable = new GiftsTaskVariable();
        BeanUtils.copyProperties(form,taskVariable);
        taskVariable.setUserId(userId);
        variables.put("taskVariable",taskVariable);
        String processInstanceId = task.getProcessInstanceId();
        log.info("process instance id: {}, application id: {}", processInstanceId,applicationId);
        if(Constant.GIVING_GIFTS_TYPE.equals(processType)){
            GivingGiftsActivityEntity activity = fillInActivityEntity(processInstanceId,taskId,applicationId,
                    userId,form,GivingGiftsActivityEntity.class);
            givingGiftsActivityDao.insert(activity);
        }else if(Constant.GIVING_HOSPITALITY_TYPE.equals(processType)) {
            //TODO  save hospitality activity
            HospitalityGiftsActivityEntity activity = fillInActivityEntity(processInstanceId,taskId,applicationId,
                    userId,form, HospitalityGiftsActivityEntity.class);
        }

        taskService.claim(taskId,String.valueOf(userId));
        taskService.complete(taskId, variables);
    }


    private <T extends GiftsActivityProcessEntity> T fillInActivityEntity(String processInstanceId,
                                       String taskId,
                                       Long applicationId,Long userId,
                                       GiftsTaskFrom form, Class<T> tClass) {
        Date currentDate = new Date();
        try {
            T t = tClass.newInstance();
            t.setSfProcessInsId(Long.valueOf(processInstanceId));
            t.setSfActivityInsId(Long.valueOf(taskId));
            t.setApplicationId(applicationId);
            t.setSfUserIdSubmitter(userId);
            t.setRemark(form.getComment());
            t.setAction(form.getApprove());
            t.setCreatedDate(currentDate);
            t.setLastModifiedDate(currentDate);
            return t;
        } catch (InstantiationException e) {
            log.error("InstantiationException", e);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException", e);
        }
        return null;
    }
}
