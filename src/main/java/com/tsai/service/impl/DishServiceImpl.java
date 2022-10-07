package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.Dish;
import com.tsai.mapper.DishMapper;
import com.tsai.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
