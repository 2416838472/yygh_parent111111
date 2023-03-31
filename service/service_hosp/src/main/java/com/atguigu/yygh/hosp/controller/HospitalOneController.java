package com.atguigu.yygh.hosp.controller;

import com.atguigu.hospital.service.ApiService;
import com.atguigu.hospital.service.HospitalService;
import com.atguigu.hospital.util.*;
import com.atguigu.model.hosp.HospitalSet;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 * @author qy
 *
 */
@Api(tags = "医院管理接口")
@Controller
@RequestMapping("api/hosp")
public class HospitalOneController {


    @Autowired
    private HospitalOneService hospitalOneService;
    @Autowired
    private HospitalSetService  hospitalSetSvice;


    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> parmMap = HttpRequestHelper.switchMap(requestMap);

        //1获取医院系统传递过来的签名
        String sign = (String) parmMap.get("sign");
        //2根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) parmMap.get("hosted");
        HospitalSet hospitalSet = hospitalSetSvice.getByHoscode(hoscode);
        //3根据传递过来的医院编码，查询数据库，查询签名,并使用MD5加密
        String KeyMd5 = MD5.encrypt(hospitalSet.getSignKey());
        //4根据签名，进行签名校验
        if(!MD5.encrypt(KeyMd5).equals(sign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        hospitalOneService.save(parmMap);
        return Result.ok();
    }



}

