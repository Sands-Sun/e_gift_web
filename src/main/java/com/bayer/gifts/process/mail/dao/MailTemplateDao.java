package com.bayer.gifts.process.mail.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.mail.entity.MailPolicy;
import com.bayer.gifts.process.mail.entity.MailTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MailTemplateDao extends BaseMapper<MailTemplate> {


    List<MailPolicy> selectMailPolicyList(Collection<Long> templateIds);
}
