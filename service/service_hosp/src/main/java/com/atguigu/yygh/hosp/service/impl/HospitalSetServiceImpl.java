package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.model.hosp.HospitalSet;
import com.atguigu.vo.hosp.HospitalSetQueryVo;
import com.atguigu.yygh.exception.YyghException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 * 医院设置表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {


    @Resource
    private HospitalSetMapper hospitalSetMapper;
    @Override
    public Page<HospitalSet> selectHospPage(Page<HospitalSet> pageParam, HospitalSetQueryVo hospitalSetQueryVo) {
        QueryWrapper wrapper = new QueryWrapper();
        if(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())
                && !StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())
        ){
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }

        Page<HospitalSet> page = hospitalSetMapper.selectPage(pageParam, wrapper);
        return page;
    }

    @Override
    public HospitalSet getByHoscode(String hoscode) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(wrapper);
        return hospitalSet;
    }

    @Override
    public String getSignKey(String hoscode) {
        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if(null == hospitalSet) {
            throw new YyghException(20001,"失败");
        }
        return hospitalSet.getSignKey();
    }
}
