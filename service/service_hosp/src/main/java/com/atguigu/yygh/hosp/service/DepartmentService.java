package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.Department;
import com.atguigu.vo.hosp.DepartmentQueryVo;
import com.atguigu.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {

    //上传科室接口
    void saveDepartment(Map<String, Object> parmMap);

    //查询科室接口
    Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo);



    //根据医院编号，查询医院所有科室
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据医院编号和科室编号，查询科室名称
    Object getDepName(String hoscode, String depcode);

    //删除科室
    void remove(String hoscode, String depcode);

    //上传科室接口
    void save(Map<String, Object> paramMap);


    Department getDepartment(String hoscode, String depcode);
}
