package com.bayer.gifts.process;

import com.bayer.gifts.process.common.validator.group.Group;
import com.bayer.gifts.process.dao.GiftsGroupDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import lombok.extern.slf4j.Slf4j;
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
public class GiftsGroupDaoTest {

    @Autowired
    GiftsGroupDao giftsGroupDao;


    @Test
    public void testQueryGroupListByUserId() {
        List<GiftsGroupEntity> groups = giftsGroupDao.queryGroupListByUserId(2975L);
    }
}
