package com.tsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsai.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
