package com.atguigu.yygh.hosp.controller;

import com.atguigu.model.hosp.Hospital;
import com.atguigu.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.hosp.service.HospitalOneService;
import com.atguigu.yygh.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author qy
 *
 */

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

	@Autowired
	private HospitalOneService hospitalService;

	//医院列表(条件查询分页)
	@GetMapping ("list/{page}/{limit}")
	public Result listHosp(@PathVariable Integer page,
						   @PathVariable Integer limit,
						   HospitalQueryVo hospitalQueryVo) {
		 Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
		 return Result.ok(pageModel);
	}

}

