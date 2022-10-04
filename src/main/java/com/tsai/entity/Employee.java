package com.tsai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 员工实体类
 *
 */
@Data
public class Employee {
    private static final long serialVersionUID = 1L;

    private long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //    @TableField(fill = FieldFill.INSERT)在进行插入（insert）时进行自动填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //    @TableField(fill = FieldFill.INSERT_UPDATE)在进行插入（insert）和更新（update）时进行自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * @TableField 公共字段自动填充， 目的：减少代码量
     */
}
