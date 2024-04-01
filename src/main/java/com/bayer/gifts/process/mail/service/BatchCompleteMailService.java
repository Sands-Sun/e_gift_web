package com.bayer.gifts.process.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.vo.BaseMailVo;

public interface BatchCompleteMailService extends IService<BatchCompleteMail> {

    void init();

    BatchCompleteMail saveCompleteMail(BaseMailVo mailVo);
}
