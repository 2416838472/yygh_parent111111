package com.atguigu.yygh.hosp.controller;

import com.atguigu.vo.hosp.DepartmentVo;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "科室管理")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // 根据医院编号，查询科室信息
    @ApiOperation(value = "查询科室信息")
    @GetMapping("findDeptTree/{hoscode}")
    public R findDeptTree(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return R.ok().data("children", list);
    }
}
