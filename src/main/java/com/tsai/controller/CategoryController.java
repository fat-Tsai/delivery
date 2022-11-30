package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.dto.CategoryDto;
import com.tsai.dto.DishDto;
import com.tsai.dto.SetmealDto;
import com.tsai.entity.Category;
import com.tsai.entity.Dish;
import com.tsai.entity.Setmeal;
import com.tsai.service.CategoryService;
import com.tsai.service.DishService;
import com.tsai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类信息列表查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        // 执行查询
        categoryService.page(pageInfo,queryWrapper);

        // 现在出现一个问题，我不知道这个分类下面有没有菜品，所以需要返回这个分类下拥有的菜品总数 2022.11.27

        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类，前提是该id所对应的分类下没有任何菜品或套餐
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> remove(@PathVariable Long id) {
        log.info("用户穿过来的category-id:{}",id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        boolean b = categoryService.updateById(category);
        if(b) {
            return R.success("菜品信息修改成功");
        }
        return R.error("菜品修改失败");
    }

    /**
     * 根据条件查询分类，菜品分类|套餐分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<CategoryDto>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc((Category::getUpdateTime));
        List<Category> list = categoryService.list(queryWrapper);
        List<CategoryDto> categoryDtoList = list.stream().map((item) -> {
            CategoryDto categoryDto = new CategoryDto();
            BeanUtils.copyProperties(item,categoryDto);
            Long categoryId = item.getId();
            Integer type = item.getType();
            if(type == 1) {
                List<DishDto> dishDtoList = dishService.getListByCategoryId(categoryId);
                categoryDto.setDishList(dishDtoList);
                categoryDto.setTotal(dishDtoList.size());
            } else {
                List<SetmealDto> setmealDtoList = setmealService.getListByCategoryId(categoryId);
                categoryDto.setSetmealList(setmealDtoList);
                categoryDto.setTotal(setmealDtoList.size());
            }
            return categoryDto;
        }).collect(Collectors.toList());
        return R.success(categoryDtoList);
    }
}
