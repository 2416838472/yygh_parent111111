package com.atguigu.yygh.user.service.impl;

import com.atguigu.model.user.UserInfo;
import com.atguigu.vo.user.LoginVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.user.helper.JwtHelper;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired

    private UserInfoMapper userInfoMapper;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //手机号+验证码 校验参数
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "手机号或验证码为空");
        }

        //2、校验校验验证码 TODO

        //**手机号+验证码登录逻辑**
        //3、检查手机号是否已被使用
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("phone", phone);
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        if(null == userInfo) {
            //4、如果手机号未被使用，根据手机号创建用户
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setName(phone);
            userInfo.setStatus(1);
            userInfoMapper.insert(userInfo);
        }

        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(20001, "用户被禁用");
        }

        //返回页面name和token
        Map<String,Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        map.put("name", name);
        map.put("token", userInfo.getId());
        return map;
    }
}
