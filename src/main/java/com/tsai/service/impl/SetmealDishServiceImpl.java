package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.SetmealDish;
import com.tsai.mapper.SetmealDishMapper;
import com.tsai.service.SetmealDishService;
import com.tsai.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
