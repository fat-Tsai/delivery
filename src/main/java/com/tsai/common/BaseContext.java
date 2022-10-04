package com.tsai.common;

/**
 * 基于ThreadLocal封装工具类，用户爆粗和获取当前登录用户id
 */
public class BaseContext {
    private  static  ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();
    public static void  setCurrentId(Long id) {
        threadLocal.set(id);
    }
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
