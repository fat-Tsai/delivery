package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.dto.DishDto;
import com.tsai.entity.Category;
import com.tsai.entity.Dish;
import com.tsai.service.CategoryService;
import com.tsai.service.DishFlavorService;
import com.tsai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.savaWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 获取菜品列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器对象
        Page<Dish>  pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo,queryWrapper);


        // 对象拷贝:拷贝pageInfo中有的属性，或者条数
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 对象拷贝:拷贝具体信息
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); //拿分类id去找分类名称
            // 根据id查询分类
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 获取菜品的信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 更改菜品状态--批量操作
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateState(@PathVariable int status, String ids) {
        log.info("ids: {}",ids);
        // 将多个ids存放到list集合中
        String[] split = ids.trim().split(",");
        List<Long> list = new ArrayList<>();
        for(int i = 0; i < split.length; i++) {
            list.add(Long.parseLong(split[i]));
        }
        // 修改状态
        dishService.updateState(list,status);
        return R.success(status == 0 ? "停售成功" : "起售成功");
    }

    /**
     * 根据菜品id,删除对应菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        // ids是否为空
        if(StringUtils.isEmpty(ids.trim())) {
            return R.error("批量操作，请先勾选菜品");
        }

        // 将多个ids存放到list集合中
        String[] split = ids.trim().split(",");
        List<Long> list = new ArrayList<>();
        for(String id : split) {
            list.add(Long.parseLong(id));
        }
        try {
            dishService.deleteWithFlavor(list);
        }catch (Exception exception) {
            return R.error("商品正在售卖，不能删除");
        }
        return R.success("菜品删除成功");
    }
}
