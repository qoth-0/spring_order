package com.encore.ordering.securities;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilter {

//    JwtAuthFilter 호출 시 doFilter 자동 실행
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String bearerToken = ((HttpServletRequest)request).getHeader("Authorization"); // header에 들어오는 bearerToken 가져오기
        String token = null;
        if(bearerToken != null) {
//            bearer 토큰에서 토큰 값만 추출
            token = bearerToken.substring(7);
//            "mysecret"
//            추출된 토큰을 검증 후 Authentication객체 생성
        }
    }
}
