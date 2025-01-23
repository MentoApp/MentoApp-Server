package com.mentit.mento.global.web;

import com.mentit.mento.global.OctetStreamReadMsgConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final OctetStreamReadMsgConverter octetStreamReadMsgConverter;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(octetStreamReadMsgConverter);
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:5173","https://dotorit.duckdns.org")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .exposedHeaders("Authorization", "Set-Cookie", "Authorization-Access","Authorization-Refresh") // 클라이언트에서 사용할 헤더 추가
                        .allowCredentials(true)
                        .maxAge(3600);
                ;
            }
        };
    }
}