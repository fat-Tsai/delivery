package com.tsai.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsai.common.R;
import com.tsai.dto.UserDto;
import com.tsai.entity.Employee;
import com.tsai.entity.User;
import com.tsai.service.UserService;
import com.tsai.utils.JWTUtils;
import com.tsai.utils.WXUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private String getPhoneNumberUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    private String getAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";

    private String jscode2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    private String appid = "wxd06fd877a3485e08";

    private String secret = "8cbf683133e689f4340ecb6e5b5869a6";

    private WXUtils wxUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/wxLogin")
    public R<String> getOpenId(@RequestBody Map<String,Object> codeBody) {
//        https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code 申请openId
        //        这一部分报错：因为数据注入失败
//        String url = wxUtils.getJscode2sessionUrl()+
//                "?appid="+wxUtils.getAppid()+
//                "&secret="+wxUtils.getSecret()+
//                "&js_code="+code+
//                "&grant_type=authorization_code";
        System.out.println("codeBody:"+codeBody);

//        String codeForOpenId = JSON.parseObject(codeBody).get("codeForOpenId").toString();
//        String codeForPhone = JSON.parseObject(codeBody).get("codeForPhone").toString();
        String codeForOpenId = codeBody.get("codeForOpenId").toString();
        String codeForPhone = codeBody.get("codeForPhone").toString();
        // 获取用户的基础信息+openId
        String url = jscode2sessionUrl+"?appid="+appid+"&secret="+secret+"&js_code="+codeForOpenId+"&grant_type=authorization_code";
        System.out.println("获取openid的url:"+url);
        String res = HttpUtil.get(url);
        System.out.println(res);
        JSONObject jsonObject = JSON.parseObject(res);
        // 获取openid
        String openid = jsonObject.get("openid").toString();
        System.out.println("openid:"+openid);

        // 获取用户手机号
        // 获取许可access_token
        String urlForToken = getAccessTokenUrl + "appid=" + appid + "&secret=" + secret;
        String resForToken = HttpUtil.get(urlForToken);
        JSONObject jsonObject1 = JSON.parseObject(resForToken);
        // 取值 access_token
        String access_token = jsonObject1.get("access_token").toString();
        System.out.println("access_token:"+access_token);
        // access_token -> phone
        String urlPhone = getPhoneNumberUrl + access_token;
        System.out.println("获取手机号的url:"+urlPhone);
        JSONObject params = new JSONObject();
        params.put("code", codeForPhone);
        String resPhone = HttpUtil.post(urlPhone,params.toString()); // code的请求需要是JSON
        JSONObject jsonObjectPhone = JSON.parseObject(resPhone);
        Integer errcode = Integer.parseInt(jsonObjectPhone.get("errcode").toString());
        System.out.println("errcode:"+errcode);
        String phoneNumber = null;
        if(errcode == 0) { // errcode=1 网络繁忙；errcode=40029 code过期或失效
            Object phone_info = jsonObjectPhone.get("phone_info");
            JSONObject phoneInfo = JSON.parseObject(String.valueOf(phone_info));
            phoneNumber = phoneInfo.get("phoneNumber").toString();
            System.out.println("手机号："+phoneNumber);
        }
        // 查找user存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid,openid);
        User userServiceOne = userService.getOne(queryWrapper);
        User user = new User();
        if(userServiceOne == null) {
            user.setOpenid(openid);
            user.setPhone(phoneNumber);
            user.setCreateTime(LocalDateTime.now());
            userService.save(user);
            System.out.println("让我打印一下userId看看是何方神圣"+String.valueOf(user.getId()));
            // 获取token
            Map<String,String> map = new HashMap<>();
            map.put("id", String.valueOf(user.getId())); // token不能放敏感类型的信息
            // 生成token
            String token = JWTUtils.getToken(map,"wx");
            return R.success(token);
        } else { // 已经登录过,重新给他签发token
//             无痛刷新token
            long id = userServiceOne.getId();
            Map<String,String> map = new HashMap<>();
            map.put("id", String.valueOf(id)); // token不能放敏感类型的信息
            System.out.println("让我打印一下userId看看是何方神圣"+String.valueOf(id));
            // 生成token
            String token = JWTUtils.getToken(map,"wx");
            return R.success(token);
        }
    }

    @PostMapping("/getPhone")
    public R<String> getPhoneNumber(@RequestParam(value = "code") String code) {
        System.out.println("获取手机号的code:"+code);
        // 先获取 access_token
        String url = getAccessTokenUrl + "appid=" + appid + "&secret=" + secret;
        System.out.println("获取access_token的url:"+url);
        String res = HttpUtil.get(url);
        JSONObject jsonObject = JSON.parseObject(res);
        // 取值 access_token 和 expires 有效时间
        String access_token = jsonObject.get("access_token").toString();
        System.out.println("access_token："+access_token);
        String expires_in = jsonObject.get("expires_in").toString();

        // 请求手机号: 用 token换手机号
        String urlPhone = getPhoneNumberUrl + access_token;
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        String resPhone = HttpUtil.post(urlPhone, paramMap);
        JSONObject jsonObjectPhone = JSON.parseObject(resPhone);
        Integer errcode = Integer.parseInt(jsonObjectPhone.get("errcode").toString());
        if(errcode == 0) {
            Object phone_info = jsonObjectPhone.get("phone_info");
            JSONObject phoneInfo = JSON.parseObject(String.valueOf(phone_info));
            String phoneNumber = phoneInfo.get("phoneNumber").toString();
            System.out.println("手机号："+phoneNumber);
            return R.success("请求成功");
            // 保存手机号给对应的user
//            userService.saveWithId(phoneNumber);
//            return R.success(phoneNumber);
        }
//        else if (errcode == -1) {
//            return R.error("系统繁忙，请稍后重试");
//            // 返回消息： 系统繁忙
//        } else if (errcode == 40029) {
//            // 请求失败： 不合法的code（code不存在、已过期或者使用过）
//            return R.error("获取手机号出错");
//        }
        return R.error("获取手机号失败");
    }

    /**
     * 获取用户手机号
     * @param request
     * @param code
     * @return
     */
    @GetMapping("/phone")
    public R<String> getPhone(HttpServletRequest request,String code) {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        // 先判断数据库中是否有手机号
        User user = userService.getById(userId);
        String phone = user.getPhone();
        if(phone != null) {
            return R.success(phone);
        }else {
            // 服务器去微信获取手机号
            // 先获取 access_token
            String url = getAccessTokenUrl + "appid=" + appid + "&secret=" + secret;
            System.out.println("获取access_token的url:"+url);
            String res = HttpUtil.get(url);
            JSONObject jsonObject = JSON.parseObject(res);
            // 取值 access_token 和 expires 有效时间
            String access_token = jsonObject.get("access_token").toString();
            System.out.println("access_token："+access_token);
            String expires_in = jsonObject.get("expires_in").toString();

            // 请求手机号: 用 token换手机号
            String urlPhone = getPhoneNumberUrl + access_token;
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("code", code);
            String resPhone = HttpUtil.post(urlPhone, paramMap);
            JSONObject jsonObjectPhone = JSON.parseObject(resPhone);
            Integer errcode = Integer.parseInt(jsonObjectPhone.get("errcode").toString());
            if(errcode == 0) {
                Object phone_info = jsonObjectPhone.get("phone_info");
                JSONObject phoneInfo = JSON.parseObject(String.valueOf(phone_info));
                String phoneNumber = phoneInfo.get("phoneNumber").toString();
                // 把手机号存到数据库中
                user.setPhone(phone);
                userService.updateById(user);
                return R.success(phoneNumber);
            }
        }

        return R.error("系统繁忙，请稍后重试");
    }

    /**
     * 获取用户信息列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name, String phone) {

        // 1. 构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        // 2. 构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        // 2-1. 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), User::getNickName,name);
        queryWrapper.like(phone!= null, User::getPhone, phone);
        // 2-2. 添加排序条件
        queryWrapper.orderByDesc(User::getCreateTime);

        // 3. 执行查询
        userService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

}
