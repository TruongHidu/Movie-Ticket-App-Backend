package com.example.movie_ticket_.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin", "/admin/**") // Tất cả trang quản trị
                .excludePathPatterns("/admin/login");   // Trừ trang đăng nhập
    }

    /** Cookie phiên đăng nhập admin không được gửi kèm request POST từ trang web khác. */
    @Bean
    public CookieSameSiteSupplier adminSessionCookieSameSite() {
        return CookieSameSiteSupplier.ofLax().whenHasName("JSESSIONID");
    }
}
