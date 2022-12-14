package com.tsai.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */

@Data
public class R<T> {

    private Integer code; // 前端数据请求返回的状态

    private String msg; // 返回信息

    private T data; // 数据

    private Map map = new HashMap(); // 动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 200;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public static <T> R<T> error(Map map) {
        R r = new R();
        r.msg = (String) map.get("msg");
        r.code = (Integer) map.get("code");
        return r;
    }


    public R<T> add(String key, Object value) {
        this.map.put(key,value);
        return this;
    }
}

