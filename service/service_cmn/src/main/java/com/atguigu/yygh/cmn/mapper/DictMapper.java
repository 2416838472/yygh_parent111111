package com.atguigu.yygh.cmn.mapper;

import com.atguigu.model.cmn.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 医院设置表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2021-08-26
 */
@Component
public interface DictMapper extends BaseMapper<Dict> {

    List<Dict> findChildData(Long id);
}
