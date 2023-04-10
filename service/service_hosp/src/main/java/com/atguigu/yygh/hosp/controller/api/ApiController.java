package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.helper.HttpRequestHelper;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalOneService hospitalService;

    @ApiOperation("上传医院接口")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        //1、获取医院端参数
        Map<String, String[]> map = request.getParameterMap();
        //2、转换为Map<String, Object>
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        hospitalService.save1(paramMap);
        return Result.ok();
    }

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap=HttpRequestHelper.switchMap(request.getParameterMap());
        //签名校验略
        departmentService.save(paramMap);
        return Result.ok();
    }

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap=HttpRequestHelper.switchMap(request.getParameterMap());
        scheduleService.save(paramMap);
        return Result.ok();
    }  
}
