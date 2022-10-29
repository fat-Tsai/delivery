package com.tsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tsai.dto.DishDto;
import com.tsai.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish dish_flavor
    public void savaWithFlavor(DishDto dishDto);

    // 根据菜品id查询菜品信息及其对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息，同时更新口味信息，根据菜品id
    public void updateWithFlavor(DishDto dishDto);

    // 删除菜品信息及口味(单个删除)
    public void deleteWithFlavor(List<Long> ids);

    // 批量更新菜品售卖状态
    public void updateState(List<Long> list, int status);
}
