package com.tsai.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * ControllerAdvice 通知; anonotations 注解
 * ResponseBody 将结果返回成JSON数据
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常梳理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        /**
         * 这里能用全局异常处理是因为 后台登录的账号在数据库中是唯一的，而如果出现"duplicate entry"则表示重复了
         * 正常逻辑：应该在前端输入完之后用校验器校验
         */
        if(ex.getMessage().contains("Duplicate entry")) {
            String msg = "该账号已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
