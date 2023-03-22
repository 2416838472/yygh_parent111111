package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */
@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //http://localhost:8201/admin/hosp/hospitalSet/findAll
    //查询所有医院设置信息
    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAllHosp() {
        //调用service方法
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

    //医院设置删除
    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R deleteHospSet(@ApiParam(name = "id", value = "医院设置ID", required = true)
                           @PathVariable Long id) {
        //调用service方法
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("删除失败");
        }
    }

    //分页查询医院
    @ApiOperation(value = "分页查询医院")
    @PostMapping("findPageHosp/{page}/{limit}")
    public R findPageHosp(@PathVariable Long page, @PathVariable Long limit
            , @RequestBody(required = false) HospitalSetQueryVo hospitalQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        //调用方法实现分页查询
        Page<HospitalSet> pageModel = hospitalSetService.selectHospPage(pageParam, hospitalQueryVo);
        //获取总记录数
        long total = pageModel.getTotal();
        //获取list集合
        List<HospitalSet> records = pageModel.getRecords();
        return R.ok().data("total", total).data("rows", records);
    }

    //添加医院
    @ApiOperation(value = "添加医院")
    @PostMapping("saveHospSet")
    public R saveHospSet(@RequestBody HospitalSet hospitalSet) {
        //调用service方法
        boolean flag = hospitalSetService.save(hospitalSet);
        if (flag) {
            return R.ok().message("添加成功");
        } else {
            return R.error().message("添加失败");
        }
    }

    //根据id获取医院设置
    @ApiOperation(value = "根据id获取医院设置")
    @GetMapping("getHospSet/{id}")
    public R getHospSet(@PathVariable Long id) {
        //调用service方法
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item", hospitalSet);
    }

    //修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PutMapping("updateHospSet")
    public R updateHospSet(@RequestBody HospitalSet hospitalSet) {
        //调用service方法
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("修改失败");
        }
    }

    //批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public R batchRemoveHospSet(@RequestBody List<Long> idList) {
        //调用service方法
        hospitalSetService.removeByIds(idList);
        return R.ok().message("删除成功");
    }

    //锁定签名
    @ApiOperation(value = "锁定签名")
    @PutMapping("lockHospSet/{id}/{status}")
    public R lockHospSet(@PathVariable Long id, @PathVariable Integer status) {
        //调用service方法
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok().message("修改成功");
    }
}

