package com.atguigu.yygh.hosp.service;

import com.atguigu.model.hosp.HospitalSet;
import com.atguigu.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */
public interface HospitalSetService extends IService<HospitalSet> {

    Page<HospitalSet> selectHospPage(Page<HospitalSet> pageParam, HospitalSetQueryVo hospitalSetQueryVo);

    HospitalSet getByHoscode(String hoscode);

    String getSignKey(String hoscode);
}
