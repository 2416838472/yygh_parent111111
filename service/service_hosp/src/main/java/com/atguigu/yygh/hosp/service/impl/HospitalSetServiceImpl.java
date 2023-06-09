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

    //查询医院设置表所有信息
    @Override
    public Page<HospitalSet> selectHospPage(Page<HospitalSet> pageParam, HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = hospitalSetMapper.selectPage(pageParam, new QueryWrapper<HospitalSet>()
                .like("hosname", hospitalSetQueryVo.getHosname())
                .eq("hoscode", hospitalSetQueryVo.getHoscode()));
        return page;
    }


    //根据医院编号查询
    @Override
    public HospitalSet getByHoscode(String hoscode) {
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(new QueryWrapper<HospitalSet>()
                .eq("hoscode", hoscode));
        return hospitalSet;
    }

    //根据医院编号获取签名
    @Override
    public String getSignKey(String hoscode) {
        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if(null == hospitalSet) {
            throw new YyghException(20001,"失败");
        }
        return hospitalSet.getSignKey();
    }
}
