package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    void deleteDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    Department findByHoscodeAndDepcode(String hoscode, String depcode);
}
