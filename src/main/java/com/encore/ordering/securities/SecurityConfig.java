package com.encore.ordering.securities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // @Configuration 포함되어 있음
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize를 사용하기 위해(특정 권한에게만 서비스 접근 허용)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    //    PasswordEncoder 생성 (암호화)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //    필터 체인 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable() // csrf 보안 사용 x - RESTAPI에서는 구조적으로 csrf공격이 어려움
                .cors().and() // CORS 활성화 - 특정 도메인만 허용
                .httpBasic().disable() // http에 대한 기본 authentication은 사용 x - 직접 커스텀할 것
                .authorizeRequests() // antMatchers의 url은 로그인 안해도 접근 가능
                    .antMatchers("/member/create", "/items", "/item/*/image", "/doLogin") // doLogin 직접 구현할 것
                    .permitAll()
                .anyRequest().authenticated() // 그 외 요청은 authenticated함(Authentication 객체 필요)
                .and()
//                세션을 사용하지 않겠다
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                JwtAuthFilter 실행 - 커스텀 필터이므로 antMatchers로 설정한 api도 들어가게 됨
//                formlogin에 사용되는 UsernamePasswordAuthenticationFilter과 같은 기능을 하므로 addFilterBefore로 순서 맞춰줌
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // User
                .build();
    }
}
