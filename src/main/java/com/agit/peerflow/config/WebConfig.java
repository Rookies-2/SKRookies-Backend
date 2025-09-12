package com.agit.peerflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * @author 백두현
 * @version 1.0
 * @since 2025-09-07
 * @description
 * 글로벌 CORS(Cross-Origin Resource Sharing) 설정 클래스
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // 리액트 로컬 주소
                .allowedOrigins("http://localhost:8080")
                .allowedOrigins("http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키 등 자격 증명 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프로젝트 루트/uploads 를 /files/** 로 노출
        String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads")
                .toFile().getAbsolutePath();

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}