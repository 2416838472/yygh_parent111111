package com.atguigu.msm.service.impl;

import com.atguigu.msm.service.MsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MsmServiceImpl implements MsmService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    public void sendCode(String phone) {

    }
}
