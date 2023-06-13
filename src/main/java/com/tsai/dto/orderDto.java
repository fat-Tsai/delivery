package com.tsai.dto;

import lombok.Data;

@Data
public class orderDto {
    private String date;
    private Integer num;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
