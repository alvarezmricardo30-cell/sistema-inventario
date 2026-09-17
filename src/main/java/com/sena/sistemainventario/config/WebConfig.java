package com.sena.sistemainventario.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/productos/**", "/exportar/**")
                .excludePathPatterns("/api/login", "/api/registro", "/api/sesion",
                        "/api/logout", "/css/**", "/js/**", "/img/**");
    }
}
