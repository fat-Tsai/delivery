package com.tsai.dto;

import com.tsai.entity.Setmeal;
import com.tsai.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
