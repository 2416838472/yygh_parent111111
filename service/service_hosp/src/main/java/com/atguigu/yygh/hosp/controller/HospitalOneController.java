package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.Department;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.DepartmentQueryVo;
import com.atguigu.vo.hosp.DepartmentVo;
import com.atguigu.vo.hosp.ScheduleQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.Result;
import com.atguigu.yygh.util.MD5;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp")
public class HospitalOneController {


    @Autowired
    private HospitalOneService hospitalOneService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService saveSchedule;



    //根据医院编号查询
    @ApiOperation(value = "根据医院编号查询")
    @PostMapping("hospital/show")
    public Result getHospitalByCode(@RequestParam("hoscode") String hoscode, @RequestParam("sign") String sign) {
        // 根据医院编号查询数据库，查询签名
        Hospital hospital = hospitalOneService.getHosp(hoscode);
        if (hospital == null) {
            throw new YyghException(20002,"医院不存在");
        }
        // 根据医院编号查询数据库，查询签名,并使用MD5加密
        String hospitalSet = hospital.toString();
        String KeyMd5 = MD5.encrypt(hospitalSet);
        // 根据签名，进行比较
        if (!KeyMd5.equals(sign)) {
            throw new YyghException(20001, "签名错误");
        }
        return Result.ok(hospital);
    }



    //分页查询科室接口
    @ApiOperation(value = "分页查询科室接口")
    @PostMapping("department/list")
    public Result findDepartment(@RequestParam(value = "hoscode") String hoscode,
                                 @RequestParam(value = "depcode") String depcode,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "limit", defaultValue = "1") Integer limit) {
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    //删除科室接口
    @ApiOperation(value = "删除科室接口")
    @PostMapping("department/remove")
    public Result removeDepartment(@RequestParam String hoscode, @RequestParam String depcode) {
        //参数校验
        if (hoscode == null || depcode == null) {
            throw new YyghException(200004, "参数为空");
        }
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }



    //查询排班接口
    @ApiOperation(value = "查询排班接口")
    @PostMapping("schedule/list")
    public Result findSchedule(@RequestBody(required = false) Map<String, String> requestMap) {
        //参数校验
        String hoscode = requestMap.get("hoscode");
        String depcode = requestMap.get("depcode");
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(depcode)) {
            throw new YyghException(200004, "参数为空");
        }
        int page = StringUtils.isEmpty(requestMap.get("page")) ? 1 : Integer.parseInt(requestMap.get("page"));
        int limit = StringUtils.isEmpty(requestMap.get("limit")) ? 1 : Integer.parseInt(requestMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = saveSchedule.selectPage(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }


    //删除排班
    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result removeSchedule(@RequestParam("hoscode") String hoscode, @RequestParam("hosScheduleId") String hosScheduleId) {
        //参数校验
        if (hoscode == null || hosScheduleId == null) {
            throw new YyghException(200004, "参数为空");
        }
        saveSchedule.remove(hoscode, hosScheduleId);
        return Result.ok();
    }


    //根据医院编号获取科室
    @ApiOperation(value = "根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}

