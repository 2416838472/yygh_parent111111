package com.atguigu.yygh.user.controller;


import com.atguigu.vo.user.LoginVo;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api("登录管理")
@RestController
@CrossOrigin
@RequestMapping("/admin")
public class UserInfoApiController {


    @Autowired
    private UserInfoService userInfoService;



    // 会员登录
    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo) {
        Map<String,Object> token = userInfoService.login(loginVo);
        return R.ok().data("token", token);
    }

    //获取会员信息
    @ApiOperation(value = "获取会员信息")
    @PostMapping("auth/getUserInfo")
    public R getUserInfo() {
        return R.ok();
    }


    //退出

    @ApiOperation(value = "退出登录")
    @PostMapping("logout")
    public R logout() {
        return R.ok();
    }




}
