package com.bayer.gifts.process;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.dao.GivingGiftsApplicationDao;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.param.OrderByParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@Slf4j
public class GiftsApplicationDaoTest {

    @Autowired
    GivingGiftsApplicationDao giftsApplicationDao;

    @Test
    public void testQueryApplicationById() {
//        List<GivingGiftsApplicationEntity> applications =
//                giftsApplicationDao.queryApplicationById(4130L);
//        for(GivingGiftsApplicationEntity application : applications){
//            System.out.println(application.getApplicationId());
//        }
    }

    @Test
    public void testQueryApplicationList() {
        GiftsApplicationParam param = new GiftsApplicationParam();
        param.setUserId(2933l);
        param.setStatus(Arrays.asList("Documented","Cancelled","Rejected","Approved","Draft"));
        OrderByParam order = OrderByParam.builder().type("desc").column("created_date").build();
        param.setOrders(Collections.singletonList(order));
        IPage<GivingGiftsApplicationEntity> page = giftsApplicationDao.queryGivingGiftsApplicationList(new Page<>(1,2),param);
        System.out.println(page);
    }
}
