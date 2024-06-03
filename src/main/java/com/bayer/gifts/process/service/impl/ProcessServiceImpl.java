package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.*;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.param.GiftsActivityParam;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.service.ProcessService;
import com.bayer.gifts.process.utils.ShiroUtils;
import com.bayer.gifts.process.variables.GiftsTaskVariable;
import com.bayer.gifts.process.vo.TaskInstanceVo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("processService")
public class ProcessServiceImpl implements ProcessService {


    @Autowired
    HistoryService historyService;

    @Autowired
    TaskService taskService;

    @Autowired
    GivingGiftsApplicationDao givingGiftsApplicationDao;
    @Autowired
    GivingHospitalityApplicationDao givingHospitalityApplicationDao;
    @Autowired
    GivingGiftsActivityDao givingGiftsActivityDao;
    @Autowired
    GivingHospitalityActivityDao givingHospitalityActivityDao;

    @Autowired
    GiftsProcessDao processDao;

    @Override
    public List<TaskInstanceVo> getHistoricAndExecActTaskList(Long applicationId,String processInsId, String processType) {
        List<TaskInstanceVo>  execTaskList = processDao.queryTaskByProcessInsId(processInsId,processType);
        List<HistoricActivityInstance> historyActList =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInsId)
                        .activityType("userTask")   //用户任务
                        .finished()       //已经执行的任务节点
                        .orderByHistoricActivityInstanceEndTime()
                        .asc()
                        .list();
        if(CollectionUtils.isNotEmpty(historyActList)){
            List<String> sfActivityInsId = historyActList.stream().map(
                    HistoricActivityInstance::getTaskId).collect(Collectors.toList());
            log.info("sfActivityInsId >>>> {}",sfActivityInsId);
            List<? extends GiftsActivityProcessEntity> actList = null;
            GiftsActivityParam param = GiftsActivityParam.builder()
                    .applicationId(applicationId)
                    .sfActivityInsIds(sfActivityInsId).build();
            if(Constant.GIVING_GIFTS_REQUEST_TYPE.equals(processType)){
                actList = givingGiftsApplicationDao.queryGivingGiftsActivityList(param);
            }else if(Constant.GIVING_HOSPITALITY_REQUEST_TYPE.equals(processType)) {
                actList = givingHospitalityApplicationDao.queryGivingHospitalityActivityList(param);
            }

            if(CollectionUtils.isNotEmpty(actList)){
                List<TaskInstanceVo> histTaskInstList = actList.stream().map(a -> {
                    TaskInstanceVo inst = new TaskInstanceVo();
                    BeanUtils.copyProperties(a, inst);
                    inst.setTaskId(String.valueOf(a.getSfActivityInsId()));
                    inst.setApplicationId(String.valueOf(applicationId));
                    inst.setRequestType(processType);
                    inst.setSfUserAppliedEmail(a.getUserEmail());
                    inst.setSfUserIdAppliedFor(a.getSfUserIdSubmitter());
                    return inst;
                }).collect(Collectors.toList());

                execTaskList.addAll(histTaskInstList);
            }
        }
        return execTaskList;
    }

    @Override
    public Long getCurrRunTaskCount(GiftsTaskParam param) {
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        List<GiftsGroupEntity> groups = user.getGroups();
        if(CollectionUtils.isNotEmpty(groups)){
            List<String> groupIds = groups.stream().map(GiftsGroupEntity::getId).collect(Collectors.toList());
            log.info("group ids >>>> {}",groupIds);
            param.setGroupIds(groupIds);
        }
        return processDao.queryTaskCount(param);
    }

    @Override
    public Pagination<TaskInstanceVo> getTaskList(GiftsTaskParam param) {
        log.info("get task page...");
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        param.setUserId(Objects.isNull(param.getUserId()) ? user.getSfUserId() : param.getUserId());
        List<GiftsGroupEntity> groups = user.getGroups();
        if(CollectionUtils.isNotEmpty(groups)){
           List<String> groupIds = groups.stream().map(GiftsGroupEntity::getId).collect(Collectors.toList());
           log.info("group ids >>>> {}",groupIds);
           param.setGroupIds(groupIds);
        }
        if(CollectionUtils.isEmpty(param.getOrders())){
            log.info("default order by APPLICATION_DATE...");
            param.setOrders(Collections.singletonList(OrderByParam.builder().column("APPLICATION_DATE").type("DESC").build()));
        }
        Page<TaskInstanceVo> pagination = new Page<>(param.getCurrentPage(), param.getPageSize());
        pagination.setSearchCount(false);
        long totalCount = processDao.queryTaskCount(param);
        IPage<TaskInstanceVo> page = processDao.queryTaskList(pagination,param);
        page.setTotal(totalCount);
        return new Pagination<>(page);
    }

    @Override
    public void handleTask(GiftsTaskFrom form) {
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        //mock begin >>>>>
//        UserExtensionEntity user = shiroService.queryUser(form.getUserId());
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
        if(Constant.GIVING_GIFTS_REQUEST_TYPE.equals(processType)){
            GivingGiftsActivityEntity activity = fillInActivityEntity(processInstanceId,taskId,applicationId,
                    userId,form,GivingGiftsActivityEntity.class);
            givingGiftsActivityDao.insert(activity);
        }else if(Constant.GIVING_HOSPITALITY_REQUEST_TYPE.equals(processType)) {
            HospitalityActivityEntity activity = fillInActivityEntity(processInstanceId,taskId,applicationId,
                    userId,form, HospitalityActivityEntity.class);
            givingHospitalityActivityDao.insert(activity);
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
