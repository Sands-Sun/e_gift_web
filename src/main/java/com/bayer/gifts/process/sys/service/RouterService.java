package com.bayer.gifts.process.sys.service;


import com.bayer.gifts.process.sys.entity.RouterEntity;

import java.util.List;

public interface RouterService {
    List<RouterEntity> getRoutes(Long userId);
}
