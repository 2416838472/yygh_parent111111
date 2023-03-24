package com.atguigu.yygh.cmn.controller;

import com.atguigu.model.cmn.Dict;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictContreoller {

    @Resource
    private DictService dictService;

    //导出数据字典接口
    @GetMapping("exportData")
    public R exportData(HttpServletResponse response){
        dictService.exportData(response);
        return R.ok();
    }

    //导入数据字典接口
    @GetMapping("importData")
    public R importData(){
        dictService.importData();
        return R.ok();
    }

    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return R.ok().data("list",list);
    }

}

