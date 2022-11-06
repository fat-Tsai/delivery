package com.tsai.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

/**
 * 七牛云工具类
 */
@Slf4j
public class QiniuUtils {

    private static String accessKey =  "alQqZMtkycJQKveHA1-63yT3seMGBfMbARZ0Vm7b";

    private static String secretKey = "LNP31Yazh1YILF3Ze09vU1S1IqatRf0L9AotHNs0";

    private static String bucket = "delivery9979";

    /**
     * 上传图片到七牛云
     * @param filePath
     * @param fileName
     */
    public static void upload2Qiniu(byte[] filePath, String fileName) {
        log.info("filePath:{}",filePath);
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(filePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key); // 如果key传进去的时候就是null,那出来的key值会和hash值一样，图片下载或者查看： 服务器域名+key
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
}
