package com.tsai.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BackOrder implements Serializable {
    private String time;
    private Integer num;

    private Double benefit;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getBenefit() {
        return benefit;
    }

    public void setBenefit(Double benefit) {
        this.benefit = benefit;
    }
}
