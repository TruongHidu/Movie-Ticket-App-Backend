package com.example.movie_ticket_.config;

import com.example.movie_ticket_.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Những API không cần token
                        .requestMatchers(
                                "/api/auth/**",   // login/register
                                "/admin/**",
                                "/api/movies/**",
                                "/api/schedules/**"
                        ).permitAll()
                        // Những API cần token
                        .requestMatchers("/api/seats/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // ⚠️ Quan trọng: Đặt filter JWT trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
//package com.example.movie_ticket_.config;
//
//import com.example.movie_ticket_.security.JwtFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtFilter jwtFilter;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//                        // 1. PUBLIC APIS (Không cần token)
//                        .requestMatchers(
//                                "/api/auth/**",     // login/register
//                                "/api/movies/**",   // movie list/details
//                                "/api/schedules/**", // schedules list/details
//                                "/admin/**"         // Admin login/setup (tạm thời)
//                        ).permitAll()
//
//                        // 2. USER APIS (Yêu cầu token và quyền ROLE_USER)
//                        // FIX TỐT NHẤT: Thay authenticated() bằng hasRole("USER")
//                        .requestMatchers(
//                                "/api/seats/**",
//                                "/api/users/profile"
//                        ).hasRole("USER")
//
//                        // 3. Mọi request còn lại yêu cầu phải có token
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                );
//
//        // Đặt filter JWT trước UsernamePasswordAuthenticationFilter
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
