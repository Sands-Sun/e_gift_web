package com.bayer.gifts.process.service;

import java.util.Date;

public interface LoadResourceService {

    void load();

    void refreshDisableSentMail(Date refreshDate);

    void refreshGiftGroup(String groupId);
}
