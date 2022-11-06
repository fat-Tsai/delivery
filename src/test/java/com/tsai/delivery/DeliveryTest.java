package com.tsai.delivery;

import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
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

    /**
     * 上传图片
     */
    @Test
    public void upload() {
        Configuration cfg = new Configuration(Region.region2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "alQqZMtkycJQKveHA1-63yT3seMGBfMbARZ0Vm7b";
        String secretKey = "LNP31Yazh1YILF3Ze09vU1S1IqatRf0L9AotHNs0";
        String bucket = "delivery9979";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "D://img//2e95bbb5-3979-49cf-ac0f-b3b3f9c82ee8.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "tsai123.jpg";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
//        try {
//            Response response = uploadManager.put(localFilePath, key, upToken);
//            //解析上传成功的结果
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
//        } catch (QiniuException ex) {
//            Response r = ex.response;
//            System.err.println(r.toString());
//            try {
//                System.err.println(r.bodyString());
//            } catch (QiniuException ex2) {
//                //ignore
//            }
//        }
    }
}
