package com.atguigu.service_user_client;

import com.atguigu.model.user.Patient;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "service-user")
public interface PatientFeignClient {
    //获取就诊人
    @GetMapping("/api/user/patient/inner/get/{id}")
    Patient getPatient(@PathVariable("id") Long id);



    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(@PathVariable("scheduleId") String scheduleId, @PathVariable("patientId") Long patientId);

    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    Patient getPatientOrder(@PathVariable("patientId") Long patientId);
}
