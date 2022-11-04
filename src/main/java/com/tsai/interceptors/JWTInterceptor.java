package com.tsai.interceptors;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsai.common.BaseContext;
import com.tsai.common.R;
import com.tsai.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器：进行登录验证
 * 拦截器写完需要进行配置
 */
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");

        log.info("拦截到请求： {}", request.getRequestURI()); // URI指的是controller中的接口
        log.info("拦截到请求： {}", request.getRequestURL()); // URL指的是完整的接口：网址+端口+接口
        System.out.println("执行了拦截器的preHandle方法");

        Map<String,Object> map = new HashMap<>();

        try {
            JWTUtils.verify(token);
            Long tokenId = JWTUtils.getTokenId(token);
            BaseContext.setCurrentId(tokenId);
            return true;
        } catch (SignatureVerificationException e) {
            map.put("msg", "签名不一致");
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            map.put("code", 401);
            map.put("msg", "token过期");
        } catch (AlgorithmMismatchException e) {
            map.put("msg", "算法不匹配");
        } catch (InvalidClaimException e) {
            map.put("msg", "失效的payload");
        } catch (Exception e) {
            map.put("msg", "token无效");
        }

        // 把map用JSON形式往后传
        String json = new ObjectMapper().writeValueAsString(map);
        log.info("json:{}",json);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String token = request.getHeader("token");
        Long userId = JWTUtils.getTokenId(token);
        BaseContext.setCurrentId(userId);

//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
