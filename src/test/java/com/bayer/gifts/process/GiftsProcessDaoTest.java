package com.bayer.gifts.process;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.dao.GiftsProcessDao;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.vo.TaskInstanceVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsProcessDaoTest {

    @Autowired
    private GiftsProcessDao processDao;

    @Autowired
    TaskService taskService;

    @Test
    public void testTaskList() {
        GiftsTaskParam param = new GiftsTaskParam();
        param.setUserId(3471L);
        IPage<TaskInstanceVo> list = processDao.queryTaskList(new Page<>(1,2),param);
        System.out.println(list);
    }

    @Test
    public void testTaskAssignee() {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("41811").list();
    }
}
