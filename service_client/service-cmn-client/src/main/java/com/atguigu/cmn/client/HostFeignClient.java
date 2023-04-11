package com.atguigu.cmn.client;


import com.atguigu.yygh.result.R;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
public interface HostFeignClient {

    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable(value = "id") String id);

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("{hoscode}")
    public R item(@PathVariable(value = "hoscode") String hoscode);
}
