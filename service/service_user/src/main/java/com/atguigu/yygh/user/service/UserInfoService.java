package com.atguigu.yygh.user.service;

import com.atguigu.model.user.UserInfo;
import com.atguigu.vo.user.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    Map<String, Object> login(LoginVo loginVo);
}
