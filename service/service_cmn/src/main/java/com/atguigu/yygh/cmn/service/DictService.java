package com.atguigu.yygh.cmn.service;

import com.atguigu.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */


public interface DictService extends IService<Dict> {


    List<Dict> findChildData(Long id);


    void exportData(HttpServletResponse response);


    void importData(MultipartFile file);

    String getName(String dictCode, String value);

    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
