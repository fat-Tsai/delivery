package com.tsai.entity;

import lombok.Data;

import java.util.List;

/**
 * 返回简单的口味数据
 */
@Data
public class Flavor {
    private  static final long serialVersionUID = 1L;
    private Long id;
    private String name;// 温度、甜度
    private List<Object> value;// 温度的分类或甜度的分类
    private Integer isActive;
}
