package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.Schedule;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    //根据医院编号和科室编号，查询排班规则数据
    @ApiOperation(value = "根据医院编号和科室编号，查询排班规则数据")
    @RequestMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page,
                                  @PathVariable Long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String,Object> pageModel = scheduleService.findScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(pageModel);
    }

}
