package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.Department;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.model.hosp.Schedule;
import com.atguigu.vo.hosp.DepartmentQueryVo;
import com.atguigu.vo.hosp.ScheduleQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.helper.HttpRequestHelper;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.result.Result;
import com.atguigu.yygh.util.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Api(tags = "医院管理接口")
@Controller
@RequestMapping("api/hosp")
public class HospitalOneController {


    @Autowired
    private HospitalOneService hospitalOneService;
    @Autowired
    private HospitalSetService hospitalSetSvice;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService saveSchedule;


    //根据医院编号查询
    @ApiOperation(value = "根据医院编号查询")
    @PostMapping("hospital/show")
    public Result getHosp(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(requestMap);
        // 获取医院编号
        String hoscode = (String) parmMap.get("hoscode");
        // 获取签名
        String sign = (String) parmMap.get("sign");
        // 根据医院编号查询数据库，查询签名
        String hospitalSet = String.valueOf(hospitalSetSvice.getByHoscode(hoscode));
        // 根据医院编号查询数据库，查询签名,并使用MD5加密
        String KeyMd5 = MD5.encrypt(hospitalSet);
        // 根据签名，进行比较
        if (!KeyMd5.equals(sign)) {
            throw new YyghException(20001, "签名错误");
        }
        // 调用service方法
        Hospital hospital = hospitalOneService.getHosp(parmMap);
        return Result.ok(hospital);
    }


    //上传医院接口
    @ApiOperation(value = "上传医院接口")
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        HttpRequestHelper.switchMap(requestMap);
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名
        String sign = (String) parmMap.get("sign");
        //2根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parmMap.get("hosted");

        String hospitalSet = String.valueOf(hospitalSetSvice.getByHoscode(hoscode));
        //3根据传递过来的医院编码，查询数据库，查询签名,并使用MD5加密
        String KeyMd5 = MD5.encrypt(hospitalSet);
        //4根据签名，进行签名校验
        if (!KeyMd5.equals(sign)) {
           throw  new YyghException(20001, "签名错误");
        }

        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String) parmMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        parmMap.put("logoData", logoData);


        hospitalOneService.save(parmMap);
        return Result.ok();
    }


    //上传科室接口
    @ApiOperation(value = "上传科室接口")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名
        String sign = (String) parmMap.get("sign");
        //2根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parmMap.get("hosted");
        String hospitalSet = String.valueOf(hospitalSetSvice.getByHoscode(hoscode));
        //3根据传递过来的医院编码，查询数据库，查询签名,并使用MD5加密
        String KeyMd5 = MD5.encrypt(hospitalSet);
        //4根据签名，进行签名校验
        if (!KeyMd5.equals(sign)) {
            throw new YyghException(20001, "签名错误");
        }

        departmentService.saveDepartment(parmMap);
        return Result.ok();
    }

    //分页查询科室接口
    @ApiOperation(value = "分页查询科室接口")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //参数校验
        String hoscode = (String) parmMap.get("hoscode");
        String depcode = (String) parmMap.get("depcode");
        if (hoscode == null || depcode == null) {
            throw new YyghException(200004, "参数为空");
        }
        int page = StringUtils.isEmpty(parmMap.get("page")) ? 1 : Integer.parseInt((String) parmMap.get("page"));
        int limit = StringUtils.isEmpty(parmMap.get("limit")) ? 1 : Integer.parseInt((String) parmMap.get("limit"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    //删除科室接口
    @ApiOperation(value = "删除科室接口")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //参数校验
        String hoscode = (String) parmMap.get("hoscode");
        String depcode = (String) parmMap.get("depcode");
        if (hoscode == null || depcode == null) {
            throw new YyghException(200004, "参数为空");
        }
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    //上传排班接口
    @ApiOperation(value = "上传排班接口")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名
        String sign = (String) parmMap.get("sign");
        //2根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parmMap.get("hosted");
        String hospitalSet = String.valueOf(hospitalSetSvice.getByHoscode(hoscode));
        //3根据传递过来的医院编码，查询数据库，查询签名,并使用MD5加密
        String KeyMd5 = MD5.encrypt(hospitalSet);
        //4根据签名，进行签名校验
        if (!KeyMd5.equals(sign)) {
            throw new YyghException(20001, "签名错误");
        }

        saveSchedule.saveSchedule(parmMap);
        return Result.ok();
    }

    //查询排班接口
    @ApiOperation(value = "查询排班接口")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //参数校验
        String hoscode = (String) parmMap.get("hoscode");
        String depcode = (String) parmMap.get("depcode");
        if (hoscode == null || depcode == null) {
            throw new YyghException(200004, "参数为空");
        }
        int page = StringUtils.isEmpty(parmMap.get("page")) ? 1 : Integer.parseInt((String) parmMap.get("page"));
        int limit = StringUtils.isEmpty(parmMap.get("limit")) ? 1 : Integer.parseInt((String) parmMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = saveSchedule.selectPage(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }


    //删除排班
    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //参数校验
        String hoscode = (String) parmMap.get("hoscode");
        String hosScheduleId = (String) parmMap.get("hosScheduleId");
        if (hoscode == null || hosScheduleId == null) {
            throw new YyghException(200004, "参数为空");
        }
        saveSchedule.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}

