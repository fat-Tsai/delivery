package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.OrderDetail;
import com.tsai.mapper.OrderDetailMapper;
import com.tsai.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
