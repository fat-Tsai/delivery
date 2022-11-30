package com.tsai.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.druid.util.HttpClientUtils;
import com.sun.deploy.net.HttpUtils;
import org.springframework.web.bind.annotation.*;
import sun.net.www.http.HttpClient;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.spi.http.HttpContext;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/openId")
    public void getOpenId(@RequestParam(value = "code") String code) {
//        https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
        System.out.println("code:"+code);
        // 获取用户的基础信息+openId
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxd06fd877a3485e08&secret=8cbf683133e689f4340ecb6e5b5869a6&js_code="+code+"&grant_type=authorization_code";
//        HttpClient.New().getURLFile()
        String res = HttpUtil.get(url);
        System.out.println(res);
    }
}
