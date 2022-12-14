package com.tsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tsai.entity.Orders;

public interface OrdersService extends IService<Orders> {
    Orders submit(Orders orders);
}
