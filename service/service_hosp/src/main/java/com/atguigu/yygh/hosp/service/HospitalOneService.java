package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Hospital;
import com.atguigu.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalOneService {
    void save(Map<String, Object> parmMap);


    //根据医院编号查询
    Hospital getHosp(Map<String, Object> parmMap);


    //医院列表(条件查询分页)
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
