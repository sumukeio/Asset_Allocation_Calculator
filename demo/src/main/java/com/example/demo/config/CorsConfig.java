package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许对所有接口路径进行跨域访问
                
                // ！！！重要：将此处的 "http://localhost:5500" 替换为您前端实际运行的地址和端口 ！！！
                // 如果您使用 VS Code Live Server，可能是 http://127.0.0.1:5500 或 http://localhost:5500
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500") 
                
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                
                // 允许所有请求头
                .allowedHeaders("*") 
                
                // 允许发送 Cookies 或 HTTP 认证信息（如果需要）
                .allowCredentials(true);
    }
}