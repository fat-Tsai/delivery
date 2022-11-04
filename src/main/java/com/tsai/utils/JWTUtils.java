package com.tsai.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtils {

    private static String SIGN = "fatTsai9979";

    // token可以绑定IP，这样的安全性更高，IP地址和上次不一致就要重定向到登陆页面，如果一致，无痛刷新

    /**
     * 生成token header.payload.signature
     * @param map
     * @return
     */
    public static String getToken(Map<String,String> map) {
        log.info("SIGN:{}",SIGN);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, 60*60*7); // 默认五分钟过期

        // 创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach((key,value) -> {
            builder.withClaim(key,value);
        });
        String token = builder.withExpiresAt(instance.getTime()) // 指定令牌过期时间
                .sign(Algorithm.HMAC256(SIGN)); // sign

        return token;
    }

    /**
     * 验证token合法性,也可以从token中获取信息
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }

    /**
     * 获取token信息中的id
     * @param token
     * @return
     */
    public static Long getTokenId(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        Long id = Long.valueOf(verify.getClaim("userId").asString());
        return id;
    }
}
