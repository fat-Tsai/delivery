package com.tsai.filter;


import com.alibaba.fastjson.JSON;
import com.tsai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginChechFilter implements Filter {
    // 路径匹配器，支持通配符。这是一个工具类
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // {}表示占位符
        log.info("拦截到请求： {}", request.getRequestURI()); // URI指的是controller中的接口
        log.info("拦截到请求： {}", request.getRequestURL()); // URL指的是完整的接口：网址+端口+接口
        // 放行

        // 1. 获取本次请求的uri
        String requestURI = request.getRequestURI();

        // 定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout"
        };

        // 2.判断本次处理是否需要登录
        boolean check = check(urls, requestURI);

        // 3.不需要处理，直接放行
        if (check) {
            // 直接放行
            filterChain.doFilter(request,response);
            return;
        }

        // 4.判断登录状态,已登录,直接放行
        if (request.getSession().getAttribute("employee") != null) {
            filterChain.doFilter(request,response);
            return;
        }

        // 5.如果未登录则返回未登录结果，通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("Not login")));

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
