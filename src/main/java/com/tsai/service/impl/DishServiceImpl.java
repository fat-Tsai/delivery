package com.tsai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.common.CustomException;
import com.tsai.dto.DishDto;
import com.tsai.entity.Dish;
import com.tsai.entity.DishFlavor;
import com.tsai.mapper.DishMapper;
import com.tsai.service.DishFlavorService;
import com.tsai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void savaWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId(); // 获得菜品id

        List<DishFlavor> flavors = dishDto.getFlavors(); // 获得口味

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基础信息
        this.updateById(dishDto);

        // 删除口味表对应数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        // 添加新的口味数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        // 删除菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 查找正在售卖的菜品
        queryWrapper.eq(Dish::getStatus,1);
        // queryWrapper.in表示字段在(ids)里面的有多少个，存在的话无法删除，如果菜品正在售卖，无法删除
        queryWrapper.in(Dish::getId,ids);
        int count = this.count(queryWrapper);
        if(count > 0) {
            throw new CustomException("菜品正在售卖中，无法删除");
        }
        this.removeByIds(ids);

        // 删除口味，只需要找到对应的id
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }

    /**
     * 批量修改菜品售卖状态
     * @param ids
     * @param status
     */
    @Override
    public void updateState(List<Long> ids, int status) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        Dish dish = new Dish();
        dish.setStatus(status);
        this.update(dish,queryWrapper);
    }
}
