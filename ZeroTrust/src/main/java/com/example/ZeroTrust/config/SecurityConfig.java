package com.example.ZeroTrust.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Spring Security 설정 클래스
 * - JWT 인증 필터 등록
 * - 경로별 인증/인가 정책 설정
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화용 PasswordEncoder 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain 설정
     * - /api/** 경로는 인증 필요
     * - 나머지는 모두 허용
     * - 세션 사용 안 함(무상태)
     * - JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated() // /api/**는 인증 필요
                .anyRequest().permitAll() // 그 외는 모두 허용
            )
            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록
        return http.build();
    }
}
