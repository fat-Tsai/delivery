package com.tsai.dto;

import com.tsai.entity.Employee;

public class EmployeeDto extends Employee {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
