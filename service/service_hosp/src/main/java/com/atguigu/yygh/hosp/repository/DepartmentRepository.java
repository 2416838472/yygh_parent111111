package com.atguigu.yygh.hosp.repository;

import com.atguigu.model.hosp.Department;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    void deleteDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    Page<Department> findAll(Example<Department> example, Page<Department> pageParam);
}
