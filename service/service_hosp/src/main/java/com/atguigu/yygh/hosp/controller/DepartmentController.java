package com.atguigu.yygh.hosp.controller;

import com.atguigu.vo.hosp.DepartmentVo;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // 根据医院编号，查询科室信息
    @ApiOperation(value = "查询科室信息")
    @RequestMapping("findDeptTree/{hoscode}")
    public Result findDeptTree(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}
