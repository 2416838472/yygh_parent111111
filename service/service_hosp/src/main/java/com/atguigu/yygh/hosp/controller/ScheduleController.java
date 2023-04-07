package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.Schedule;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "排班管理")
@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    //根据医院编号和科室编号，查询排班规则数据
    @ApiOperation(value = "根据医院编号和科室编号，查询排班规则数据")
    @PostMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page,
                                  @PathVariable Long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String,Object> pageModel = scheduleService.findScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(pageModel);
    }

    // 根据医院编号、科室编号、工作日期，查询排班详细信息
    @ApiOperation(value = "根据医院编号、科室编号、工作日期，查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode, depcode, workDate);
        return Result.ok(list);
    }
}
