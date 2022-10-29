package com.tsai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.entity.Category;
import com.tsai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc((Category::getUpdateTime));
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
