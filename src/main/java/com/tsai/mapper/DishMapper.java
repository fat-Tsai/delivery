package com.tsai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsai.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
