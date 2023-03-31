package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Hospital;

import java.util.Map;

public interface HospitalOneService {
    void save(Map<String, Object> parmMap);


    //根据医院编号查询
    Hospital getHosp(Map<String, Object> parmMap);


}
