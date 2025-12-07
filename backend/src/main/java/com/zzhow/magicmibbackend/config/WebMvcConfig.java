package com.zzhow.magicmibbackend.config;

import com.zzhow.magicmibbackend.interceptor.IpAccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * @author ZZHow
 * create 2025/12/7
 * update 2025/12/7
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private IpAccessInterceptor ipAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipAccessInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/favicon.ico");
    }
}