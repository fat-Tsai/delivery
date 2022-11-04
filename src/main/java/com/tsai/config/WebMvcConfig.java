package com.tsai.config;

import com.tsai.common.JacksonObjectMapper;
import com.tsai.interceptors.JWTInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * 添加消息拦截器、请求拦截器
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//    /**
//     * 设置静态资源映射
//     * @param registry
//     */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("开始进行静态资源映射...");
//        registry.addResourceHandler("/delivery-back/**").addResourceLocations("classpath:/delivery-back/");
////        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//    }

    /**
     * 添加请求拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new JWTInterceptor());
        registration.addPathPatterns("/**"); // 拦截所有路径
        registration.excludePathPatterns( // 不需要拦截的路径
                "/employee/login",
                "/employee/logout",
                "/common/upload",
                "/common/download"
        );
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /**
     * 消息转换器
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用 Jackson 将 Java 对象转为 json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到 mvc框架的转换器集合中
        converters.add(0,messageConverter);
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

}
