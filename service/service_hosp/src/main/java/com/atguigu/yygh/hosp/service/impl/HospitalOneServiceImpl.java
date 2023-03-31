package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class HospitalOneServiceImpl implements HospitalOneService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> parmMap) {
        //把参数map集合转换对象
        String mapString = JSONObject.toJSONString(parmMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExist != null) {
            //修改
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //添加
            hospital.setStatus(1);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }
}
