package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.cmn.client.DictFeignClient;
import com.atguigu.model.hosp.Hospital;
import com.atguigu.model.hosp.HospitalSet;
import com.atguigu.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.util.MD5;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class HospitalOneServiceImpl implements HospitalOneService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DictFeignClient dictFeignClient;


    @Override
    public Hospital getHosp(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    //医院列表(条件查询分页)
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //分页
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //创建pageable对象，设置当前页和每页记录数
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);
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

    //更新医院上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Optional<Hospital> optionalHospital = hospitalRepository.findById(id);
        if (optionalHospital.isPresent()) {
            Hospital hospital = optionalHospital.get();
            //设置医院状态
            hospital.setStatus(status);
            //更新医院信息
            hospitalRepository.save(hospital);
        } else {
            throw new RuntimeException("医院不存在");
        }
    }

    //医院详情信息
    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String,Object> result = new HashMap<>();
        try {
            Hospital hospital = this.setHospitalHosType((hospitalRepository.findById(id).get()));
            result.put("hospital",hospital);
            result.put("bookingRule",hospital.getBookingRule());

            hospital.setBookingRule(null);
        } catch (NoSuchElementException e) {
            result.put("error", "找不到id为" + id + "的医院");
        }
        return result;
    }

    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null) {
            return hospital.getHosname();
        }
        return null;
    }

    //根据医院名称模糊查询
    @Override
    public List<Hospital> findByHosname(String hosname) {
        //创建mapper 实现
//        QueryWrapper wrapper = new QueryWrapper();
//        wrapper.like("hosname",hosname);
//        return .selectList(wrapper);
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    //根据医院编号获取医院预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        return getHospitalDetail(hoscode);
    }

    //获取医院详情
    private Map<String, Object> getHospitalDetail(String hoscode) {
        Map<String,Object> result = new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.getHospitalByHoscode(hoscode));
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());

        hospital.setBookingRule(null);

        return result;
    }


    private Hospital setHospitalHosType(Hospital hospital) {
        String hostype = dictFeignClient.getName("Hostype", hospital.getHostype());
        String name = dictFeignClient.getName(hospital.getProvinceCode());
        String name1 = dictFeignClient.getName(hospital.getCityCode());
        String name2 = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress", name + name1 + name2);
        hospital.getParam().put("hostypeString", hostype);
        return hospital;
    }

    @Override
    public void save(Map<String, Object> paramMap) {
        //1、获取医院端的签名（这个签名是经过md5加密）
        String sign = (String) paramMap.get("sign");

        //2、获取医院端传递的医院编号hoscode
        String hoscode = (String) paramMap.get("hoscode");
        if(StringUtils.isEmpty(hoscode)){
            throw new YyghException(20001,"医院编号不能为空");
        }

        HospitalSet hospitalSet = hospitalSetService.getByHoscode(hoscode);
        if(hospitalSet == null){
            throw new YyghException(20001,"医院编号不存在");
        }

        Integer status = hospitalSet.getStatus();
        if(status == 0){
            throw new YyghException(20001,"医院未签约");
        }

        //3、根据医院编号查询医院设置中的signKey（签名key），该签名key没有加密
        String signKey = hospitalSetService.getSignKey(hoscode);

        //4、医院端传递过来的签名key和我们自己获取的签名key比较
        if (!MD5.encrypt(signKey).equals(sign)){
            throw new YyghException(20001,"签名校验失败");
        }

        //5、参数map转成Hospital对象
        String string = JSON.toJSONString(paramMap);
        Hospital hospital = JSON.parseObject(string, Hospital.class);
        //6、设置医院对象默认的状态为1；logoData中的字符串替换成+
        hospital.setStatus(1);
        hospital.setLogoData(hospital.getLogoData().replaceAll(" ","+"));


        //7、根据hoscode去mongodb中查询该医院对象
        Hospital hp = hospitalRepository.findByHoscode(hoscode);

        //8、如果不存在，新增操作
        if(hp==null){
            //添加
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }else{
            //9、如果存在，做更新操作
            hospital.setId(hp.getId());
            hospital.setCreateTime(hp.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);//hospital必须有id主键
        }
    }
}
