package com.tsai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品口味表
 */
@Data
public class DishFlavor implements Serializable {

    private  static final long serialVersionUID = 1L;

    private Long id;

    private Long dishId;

    // 口味名称：甜度、辣度等
    private String name;

    // 口味数据
    private String value;

    // 逻辑删除标志位
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
