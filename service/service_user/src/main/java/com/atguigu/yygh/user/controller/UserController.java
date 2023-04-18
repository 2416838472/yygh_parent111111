package com.atguigu.yygh.user.controller;

import com.atguigu.model.user.UserInfo;
import com.atguigu.vo.user.UserInfoQueryVo;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//1. 用户列表（条件+分页） 2用户详情接口 3.锁定用户接口 4.解锁用户接口 5.用户认证审批接口
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    //1. 用户列表（条件+分页）
    @ApiOperation(value = "用户列表")
    @GetMapping("{page}/{limit}")
    public R index(@PathVariable Long page, @PathVariable Long limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageReult =  userInfoService.selectPage(page, limit, userInfoQueryVo);
        return R.ok();
    }

    //用户锁定
    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public R lock(@PathVariable("userId")Long userId,@PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return R.ok();
    }

    //用户详情
    @ApiOperation(value = "用户详情")
    //用户详情
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        //map=用户对象+就诊人列表
        Map<String,Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    //认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }
}
