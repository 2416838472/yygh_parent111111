package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.model.hosp.Department;
import com.atguigu.vo.hosp.DepartmentQueryVo;
import com.atguigu.vo.hosp.DepartmentVo;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {


    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> parmMap) {
        // 1.根据医院编号和科室编号查询科室是否存在
        String paraMapString = JSONObject.toJSONString(parmMap);
        Department department = JSONObject.parseObject(paraMapString, Department.class);
        Department departmentExist = departmentRepository.
                getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        // 2.如果不存在，添加科室
        if (departmentExist != null) {
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    //自己写sql语句
//    @Override
//    public Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo) {
//        Page<Department> pageParam = new Page<>(page, limit);
//        return departmentRepository.findPageDepartment(pageParam, departmentQueryVo);
//    }

    //自己写分页实现
    @Override
    public Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        //创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //创建实例
        Example<Department> example = Example.of(department, matcher);
        Page<Department> pages = departmentRepository.findAll(example,pageable);
        return pages;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department =
                departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }
    }

    //根据医院编号，查询医院所有科室
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        // 创建list集合，用于存储最终封装数据
        List<DepartmentVo> result = new ArrayList<>();
        // 创建查询条件
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        // 创建查询条件的Example对象
        Example<Department> example = Example.of(departmentQuery);
        // 根据查询条件查询所有科室
        List<Department> all = departmentRepository.findAll(example);
        // 遍历所有科室list集合，封装数据
        for (Department department : all) {
            // 判断是否是一级科室
            if (department.getDepcode().equals(department.getHoscode())) {
                // 创建一级科室的DepartmentVo对象
                DepartmentVo departmentVo = new DepartmentVo();
                departmentVo.setDepcode(department.getDepcode());
                departmentVo.setDepname(department.getDepname());
                // 将一级科室的DepartmentVo对象添加到结果集合中
                result.add(departmentVo);
            }
        }
        // 封装二级科室
        // 遍历一级科室
        for (DepartmentVo departmentVo : result) {
            // 创建二级科室的DepartmentVo对象集合
            List<DepartmentVo> children = new ArrayList<>();
            // 遍历所有科室
            for (Department department : all) {
                // 判断二级科室的hoscode和depcode是否和一级科室的hoscode和depcode相同
                if (departmentVo.getDepcode().equals(department.getHoscode()) &&
                        !departmentVo.getDepcode().equals(department.getDepcode())) {
                    // 创建二级科室的DepartmentVo对象
                    DepartmentVo child = new DepartmentVo();
                    child.setDepcode(department.getDepcode());
                    child.setDepname(department.getDepname());
                    // 将二级科室的DepartmentVo对象添加到二级科室的DepartmentVo对象集合中
                    children.add(child);
                }
            }
            // 遍历完所有科室后，将二级科室的DepartmentVo对象集合设置到一级科室的children属性中
            departmentVo.setChildren(children);
        }
        // 返回结果集合
        return result;
    }

    //根据医院编号和科室编号查询科室名称
    @Override
    public Object getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.
                getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            return department.getDepname();
        }
        return null;
    }
}
