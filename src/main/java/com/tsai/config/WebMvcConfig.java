//package com.tsai.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Slf4j
//@Configuration
//public class WebMvcConfig extends WebMvcConfigurationSupport {
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
//}
