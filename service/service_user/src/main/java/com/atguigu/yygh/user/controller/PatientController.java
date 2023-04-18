package com.atguigu.yygh.user.controller;


import com.atguigu.model.user.Patient;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.user.helper.JwtHelper;
import com.atguigu.yygh.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    //查询当前用户的就诊人列表
    @RequestMapping("auth/findAll")
    public R findAll(){


        return R.ok();
    }

    //添加就诊人
    @PostMapping("auth/save")
    public R save(@RequestBody Patient patient , HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }

    //根据id获取就诊人信息
    @GetMapping("auth/getPatientById")
    public R getPatientById(Long id){
        Patient patient = patientService.getById(id);
        return R.ok().data("patient",patient);
    }

    //修改就诊人
    @PostMapping("auth/update")
    public R update(@RequestBody Patient patient){
        patientService.updateById(patient);
        return R.ok();
    }

    //删除就诊人
    @DeleteMapping("auth/remove")
    public R remove(Long id){
        patientService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id) {
        return patientService.getById(id);
    }

}
