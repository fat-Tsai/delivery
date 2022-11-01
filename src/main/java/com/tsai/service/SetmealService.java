package com.tsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tsai.dto.SetmealDto;
import com.tsai.entity.Setmeal;
import com.tsai.mapper.SetmealMapper;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void updateWithDish(SetmealDto setmealDto);

    public SetmealDto getByIdWithDish(Long id);

    public void updateStatus(List<Long> list, int status);

    public void deleteWithDish(List<Long> list);
}
