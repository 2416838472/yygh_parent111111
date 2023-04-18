package com.atguigu.service_orders.service;

import com.atguigu.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

//创建service
public interface OrderService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);
}
