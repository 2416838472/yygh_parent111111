package com.atguigu.msm.controller;

import com.atguigu.msm.service.MsmService;
import com.atguigu.yygh.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Autowired
    private MsmService msmService;

    @GetMapping("/send/{phone}")
    public R sendCode(@PathVariable String phone){
        msmService.sendCode(phone);
        return R.ok().message("验证码发送成功");
    }
}