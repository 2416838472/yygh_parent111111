package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital,String> {


    Hospital getHospitalByHoscode(String hoscode);

    //根据医院名称查询
    List<Hospital> findHospitalByHosnameLike(String hosname);

    //根据医院编号查询
    Hospital findByHoscode(String hoscode);
}
