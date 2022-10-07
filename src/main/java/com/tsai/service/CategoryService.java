package com.tsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tsai.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
