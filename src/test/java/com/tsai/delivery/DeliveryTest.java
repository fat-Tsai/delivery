package com.tsai.delivery;

import com.auth0.jwt.JWT;
import com.tsai.common.BaseContext;
import com.tsai.utils.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeliveryTest {

    /**
     * 测试能否正确拿到id:用于更新和插入
     */
    @Test
    public void test () {
        Long tokenId = JWTUtils.getTokenId("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2Njc1ODA0NjQsInVzZXJJZCI6IjEiLCJ1c2VybmFtZSI6ImFkbWluIn0.gAH6-3_xtThCGO5HPtSU2DbJTKjaKSIq_4psovYJ2GY");
        System.out.println("token中取到的id: "+tokenId);

        BaseContext.setCurrentId(tokenId);
        System.out.println("BaseContext中的id:"+BaseContext.getCurrentId());
    }
}
