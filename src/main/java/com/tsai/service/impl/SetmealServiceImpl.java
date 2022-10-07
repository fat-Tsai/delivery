package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.Setmeal;
import com.tsai.mapper.SetmealMapper;
import com.tsai.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService{
}
