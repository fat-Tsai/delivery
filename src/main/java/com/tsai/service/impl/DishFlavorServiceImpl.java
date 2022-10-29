package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.DishFlavor;
import com.tsai.mapper.DishFlavorMapper;
import com.tsai.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
