package com.example.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 匹配所有接口
        registry.addMapping("/**")
                // 允许所有来源访问（生产环境可替换为具体前端域名）
                .allowedOriginPatterns("*")
                // 允许携带Cookie/Token
                .allowCredentials(true)
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 预检请求有效期（秒）
                .maxAge(3600);
    }
}