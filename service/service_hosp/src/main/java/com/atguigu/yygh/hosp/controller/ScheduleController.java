package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.R;
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
    public R getScheduleRule(@PathVariable Long page,
                             @PathVariable Long limit,
                             @PathVariable String hoscode,
                             @PathVariable String depcode) {
        Map<String,Object> pageModel = scheduleService.findScheduleRule(page, limit, hoscode, depcode);
        return R.ok().data(pageModel);
    }

    // 根据医院编号、科室编号、工作日期，查询排班详细信息
    @ApiOperation(value = "根据医院编号、科室编号、工作日期，查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate){
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode, depcode, workDate);
        return R.ok().data("scheduleList", list);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

}
