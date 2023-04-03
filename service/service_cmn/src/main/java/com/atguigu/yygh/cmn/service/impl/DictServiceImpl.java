package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.model.cmn.Dict;
import com.atguigu.vo.cmn.DictEeVo;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    @Resource
    private DictMapper dictMapper;


    @Override
    @Cacheable(value = "dict", key = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        return dictMapper.findChildData(id);
    }

    @Override
    public void exportData(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = null;
        try {
            fileName = URLEncoder.encode("数据字典", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        List<Dict> list = dictMapper.selectList(null);
        List<DictEeVo> dictEeVos = new ArrayList<>();
        for (Dict dict : list) {
            DictEeVo dictEeVo = new DictEeVo();
            dictEeVo.setId(dict.getId());
            dictEeVo.setParentId(dict.getParentId());
            dictEeVo.setName(dict.getName());
            dictEeVo.setValue(dict.getValue());
            dictEeVo.setDictCode(dict.getDictCode());
            dictEeVos.add(dictEeVo);
        }
        //调用方法实现写操作
        try {
            EasyExcel.write(response.getOutputStream(),
                    DictEeVo.class).sheet("数据字典").doWrite(dictEeVos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //导入数字字典
    @Override
    @CacheEvict(value = "dict", allEntries = true)
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),
                    DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
