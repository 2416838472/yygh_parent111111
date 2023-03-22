package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */
@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //http://localhost:8201/admin/hosp/hospitalSet/findAll
    //查询所有医院设置信息
    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public List<HospitalSet> findAllHosp() {
        //调用service方法
        List<HospitalSet> list = hospitalSetService.list();
        return  list;
    }

    //医院设置删除
    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public boolean deleteHospSet(@ApiParam(name = "id", value = "医院设置ID", required = true) @PathVariable Long id) {
        //调用service方法
        boolean flag = hospitalSetService.removeById(id);
        return flag;
    }

}

