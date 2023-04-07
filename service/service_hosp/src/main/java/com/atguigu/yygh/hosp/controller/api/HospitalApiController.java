package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.model.hosp.Hospital;
import com.atguigu.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.result.R;
import com.atguigu.yygh.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalApiController {
    @Autowired
    private HospitalOneService hospitalService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public R index(@PathVariable Integer page,
                   @PathVariable Integer limit,
                   HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return R.ok().data("pages", pageModel);
    }

    @ApiOperation(value = "根据医院名称获取医院列表")
    @GetMapping("findByHosname/{hosname}")
    public R findByHosname(@PathVariable String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return R.ok().data("list", list);
    }

    //更新医院上线状态
    @ApiOperation(value = "更新医院上线状态")
    @PutMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id, @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    //医院详情信息
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id) {
        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("{hoscode}")
    public R item(@PathVariable String hoscode) {
        Map<String, Object> map = hospitalService.item(hoscode);
        return R.ok().data(map);
    }


}
