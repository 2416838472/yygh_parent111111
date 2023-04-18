package com.atguigu.service_orders.mapper;

import com.atguigu.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

//创建mapper
@Repository
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
