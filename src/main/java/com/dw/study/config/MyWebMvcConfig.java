package com.dw.study.config;


import com.dw.study.ApiIdepotent.MyIntercepters.ApiIdempotentInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * @Author dw
 * @ClassName MyWebMvcConfig
 * @Description
 * @Date 2021/1/30 22:16
 * @Version 1.0
 */
@Configuration
public class MyWebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private ApiIdempotentInterceptor apiIdempotentInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiIdempotentInterceptor);
        super.addInterceptors(registry);
    }
}
