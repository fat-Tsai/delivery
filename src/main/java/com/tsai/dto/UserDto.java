package com.tsai.dto;

import com.tsai.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {

    private String code;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
