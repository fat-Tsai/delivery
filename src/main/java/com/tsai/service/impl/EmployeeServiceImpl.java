package com.tsai.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsai.entity.Employee;
import com.tsai.mapper.EmployeeMapper;
import com.tsai.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    /**
     * 判断用户是否登录成功
     * @param employee
     */
    @Override
    public void login(Employee employee) {

    }
}
