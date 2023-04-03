package com.atguigu.yygh.hosp.controller;


import com.atguigu.model.hosp.HospitalSet;
import com.atguigu.vo.hosp.HospitalSetQueryVo;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.util.MD5;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Resource
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
        //设置状态
        hospitalSet.setStatus(1);
        hospitalSet.setCreateTime(new Date());
        hospitalSet.setUpdateTime(new Date());
        //自定义生成签名密钥，并使用MD5加密
        Random random = new Random();
        hospitalSet.setSignKey
                (MD5.encrypt(System.currentTimeMillis()
                        + "" + random.nextInt(1000)));
        //调用service方法
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
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
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) {
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
        boolean b = hospitalSetService.removeByIds(idList);
        if (b) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("删除失败");
        }
    }


    //设置状态
    @ApiOperation(value = "设置状态")
    @PutMapping("status/{id}/{status}")
    public R status(@PathVariable Long id, @PathVariable Integer status) {
        //调用service方法
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("修改失败");
        }
    }

    //发送签名秘钥
//    @ApiOperation(value = "发送签名秘钥")
//    @PutMapping("sendKey/{id}")
//    public R sendKey(@PathVariable Long id) {
//        //调用service方法
////        HospitalSet hospitalSet = hospitalSetService.getById(id);
////        String signKey = hospitalSet.getSignKey();
////        String hoscode = hospitalSet.getHoscode();
////        //TODO   发送短信
////        return R.ok().message("发送成功");
//        return null;
//    }


    //q:如何使用阿里云短信服务，并引入到发送签名密钥接口当中
    //a:1.引入阿里云短信服务的依赖
    //2.在阿里云短信服务中创建签名和模板
    //3.在阿里云短信服务中获取accessKeyId和accessKeySecret
    //4.在application.yml中配置accessKeyId和accessKeySecret
    //5.在发送短信的接口中注入阿里云短信服务的工具类
    //6.调用阿里云短信服务的工具类发送短信
    //7.在阿里云短信服务中配置回调地址
    //8.在阿里云短信服务中配置短信签名和模板

    //q:阿里云短信服务依赖是什么，用pom .xml引入
    //a:aliyun-java-sdk-core


    //医院设置锁定和解锁
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable Long id, @PathVariable Integer status) {

        if (status != 1 && status != 0) {
            return R.error().message("状态值不正确");
        } else {
            //调用service方法
            HospitalSet hospitalSet = hospitalSetService.getById(id);
            hospitalSet.setStatus(status);
            boolean b = hospitalSetService.updateById(hospitalSet);
            if (b) {
                return R.ok().message("修改成功");
            } else {
                return R.error().message("修改失败");
            }
        }
    }
}

