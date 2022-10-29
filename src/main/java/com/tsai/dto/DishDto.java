package com.tsai.dto;

import com.tsai.entity.Dish;
import com.tsai.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    // 给菜品添加口味 -- 类型集合
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
