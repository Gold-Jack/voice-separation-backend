package com.voice.separation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class DefaultFeignConfig {

    /**
     * 如果要对某个服务配置logger，则在对应的controller.@FeignClient上添加configuration=...
     * 如果对所有服务配置logger，则在全局@EnableFeignClient上添加defaultConfiguration=...
     * @return
     */
//    @Bean
//    public Logger.Level logLevel() {
//        return Logger.Level.BASIC;       // 尽量用NULL或者BASIC
//    }
}