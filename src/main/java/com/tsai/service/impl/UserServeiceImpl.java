package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.User;
import com.tsai.mapper.UserMapper;
import com.tsai.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServeiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
