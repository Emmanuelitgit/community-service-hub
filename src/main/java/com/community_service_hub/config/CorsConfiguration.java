package com.community_service_hub.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfiguration implements CorsConfigurationSource {
    /**
     * @auther Emmanuel Yidana
     * @description: configurations to allow certain origins and http method types to access the server
     * @date 16-01-2025
     * @return config
     */
    @Override
    public org.springframework.web.cors.CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://rent-new.vercel.app/", "http://localhost:5216", "https://helpbridgeapp.vercel.app/"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        return config;
    }
}
