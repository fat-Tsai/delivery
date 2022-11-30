package com.tsai.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weixin")
@Data
public class WXUtils {

    private String getPhoneNumberUrl;

    private String getAccessTokenUrl;

    private String jscode2sessionUrl;

    private String appid;

    private String secret;

//    public String getOpenId(String code) {
//        String url = base_url+"sns/jscode2session?" +
//                "appid="+appid+"&secret="+secret+
//                "&js_code="+code+"&grant_type=authorization_code";
//        String res = HttpUtil.get(url);
//        JSONObject jsonObject = JSON.parseObject(res);
//        String openid = jsonObject.get("openid").toString();
//        String session_key = jsonObject.get("session_key").toString();
//        return res;
//    }

//    public String getAccessToken() {
//        String url = base_url+"cgi-bin/token?" +
//                "grant_type=client_credential&" +
//                "appid="+appid+"&secret="+secret;
//        System.out.println("获取access TOken的url:"+url);
//        String res = HttpUtil.get(url);
//        System.out.println("res:"+res);
//        return res;
//    }

//    public String getPhoneNumber(String code) {
//        String accessToken = this.getAccessToken();
//        String url = base_url+"wxa/business/getuserphonenumber?" +
//                "access_token="+accessToken;
//        System.out.println("url:"+url);
//        String res = HttpUtil.post(url, code);
//        System.out.println("phone_info -- res:"+res);
//        return null;
//    }
}
