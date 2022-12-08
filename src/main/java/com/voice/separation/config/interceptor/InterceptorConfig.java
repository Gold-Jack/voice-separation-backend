package com.voice.separation.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")      // 拦截所有请求，通过判断token是否合法，来决定是否需要登陆
                .excludePathPatterns("/**")   // 目前开发环境，暂时不启用token验证
                .excludePathPatterns("/user/login", "/user/register", "/user/logout")
                .excludePathPatterns("/admin/**");
    }



}
