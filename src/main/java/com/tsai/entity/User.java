package com.tsai.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息表
 */
@Data
public class User {

    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    private String openid;// 微信用户标识

    private String avatar;// 微信头像

    private String nickName;// 微信昵称

    private Integer integral; // 积分

    private LocalDateTime createTime;
}
