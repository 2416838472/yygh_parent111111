package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Department;
import com.atguigu.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {

    //上传科室接口
    void saveDepartment(Map<String, Object> parmMap);

    //查询科室接口
    Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo);

    //删除
    void remove(String hoscode, String depcode);
}
