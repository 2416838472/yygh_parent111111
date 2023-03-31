package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HospitalRepository extends MongoRepository<Hospital,String> {


    Hospital getHospitalByHoscode(String hoscode);
}
