package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Hospital;
import com.atguigu.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalOneService {



    //根据医院编号查询
    Hospital getHosp(String hoscode);


    //医院列表(条件查询分页)
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新医院上线状态
    void updateStatus(String id, Integer status);

    //医院详情信息
    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);

    //根据医院名称模糊查询
    List<Hospital> findByHosname(String hosname);

    //根据医院编号查询
    Map<String, Object> item(String hoscode);

    void save(Map<String, Object> paramMap);
}
