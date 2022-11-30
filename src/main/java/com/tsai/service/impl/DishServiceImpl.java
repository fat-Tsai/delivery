package com.tsai.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.common.CustomException;
import com.tsai.dto.DishDto;
import com.tsai.entity.Category;
import com.tsai.entity.Dish;
import com.tsai.entity.DishFlavor;
import com.tsai.entity.Flavor;
import com.tsai.mapper.DishMapper;
import com.tsai.service.DishFlavorService;
import com.tsai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     * 根据分类id获取该分类下的所有菜品列表及其口味数据
     *
     * @param id
     * @return
     */
    @Override
    public List<DishDto> getListByCategoryId(Long id) {
        // 构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 获取菜品id
        queryWrapper.eq(id != null,Dish::getCategoryId, id);
        // 正在售卖
        queryWrapper.eq(Dish::getStatus,1);
        // 查询条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 执行
        List<Dish> list = this.list(queryWrapper);

        // 添加口味返回数据，适配用户端
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties((item),dishDto);
//            Long categoryId = item.getCategoryId();
//            Category category = categoryService.getById(categoryId);
//            if(category != null) {
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
            // 用当前菜品的id，去获取口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            List<Flavor> flavorList = dishFlavorList.stream().map((flavors) -> {
                Flavor flavor = new Flavor();
                // 获取口味名
                String name = flavors.getName();
                flavor.setName(name);
                String value = flavors.getValue();
                JSONArray objects = JSONUtil.parseArray(value);
                System.out.println(objects);
                flavor.setValue(objects);
                Long flavorId = flavors.getId();
                flavor.setId(flavorId);
                flavor.setIsActive(0);
                return flavor;
            }).collect(Collectors.toList());
//            dishDto.setFlavors(dishFlavorList);
            dishDto.setFlavorList(flavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }

    /**
     * 测试用的菜品口味信息，目前仿sku
     * @param dishId
     * @return
     */
    @Override
    public List<Flavor> getEasyFlavorList(Long dishId) {

        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        List<Flavor> flavorList = flavors.stream().map((item) -> {
            Flavor flavor = new Flavor();
            // 获取口味名
            String name = item.getName();
            flavor.setName(name);
            String value = item.getValue();
            JSONArray objects = JSONUtil.parseArray(value);
            System.out.println(objects);
            // 需要将数组 ["微辣","中辣","重辣"] 改造成 [{id: 0,name: "微辣"}]
            flavor.setValue(objects);
            Long id = item.getId();
            System.out.println("这个id表示什么意思"+id);
            flavor.setId(id);
            return flavor;
        }).collect(Collectors.toList());
//        dishDto.setFlavors(flavors);

        return flavorList;
    }
}
