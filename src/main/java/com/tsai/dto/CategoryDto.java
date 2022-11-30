package com.tsai.dto;

import com.tsai.entity.Category;

import java.util.List;

public class CategoryDto extends Category {
    private Integer total; // 该分类拥有的菜品总数

    private List<DishDto> dishList; // 该分类下的菜品列表

    private List<SetmealDto> setmealList;

    public List<SetmealDto> getSetmealList() {
        return setmealList;
    }

    public void setSetmealList(List<SetmealDto> setmealList) {
        this.setmealList = setmealList;
    }

    public Integer getTotal() {
        return total;
    }

    public List<DishDto> getDishList() {
        return dishList;
    }

    public void setDishList(List<DishDto> dishList) {
        this.dishList = dishList;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
