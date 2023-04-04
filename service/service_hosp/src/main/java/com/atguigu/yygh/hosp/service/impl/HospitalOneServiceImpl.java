package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.cmn.client.DictFeignClient;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HospitalOneServiceImpl implements HospitalOneService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> parmMap) {
        //把参数map集合转换对象
        String mapString = JSONObject.toJSONString(parmMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExist != null) {
            //修改
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //添加
            hospital.setStatus(1);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public Hospital getHosp(Map<String, Object> parmMap) {
        String hoscode = (String) parmMap.get("hoscode");
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    //医院列表(条件查询分页)
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象，设置当前页和每页记录数
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //创建条件匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        //创建实例并匹配
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建匹配器Example
        Example<Hospital> example = Example.of(hospital, matcher);
        //调用方法实现分页查询
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);

        List<Hospital> content = all.getContent();
        for (Hospital hos : content) {
            String hostypeString = dictFeignClient.getName("Hostype", hos.getHostype());
            String provinceString = dictFeignClient.getName(hos.getProvinceCode());
            String cityString = dictFeignClient.getName(hos.getCityCode());
            String districtString = dictFeignClient.getName(hos.getDistrictCode());
            hos.getParam().put("fullAddress", provinceString + cityString + districtString);
            hos.getParam().put("hostypeString", hostypeString);
        }
        return all;
    }



}
