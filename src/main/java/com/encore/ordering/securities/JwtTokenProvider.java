package com.encore.ordering.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public String createToken(String email, String role) {
//        claims : 토큰 사용자에 대한 속성이나 데이터 포함 - 주로 페이로드를 의미
        Claims claims = Jwts.claims().setSubject(email); // Subject : pk(email)
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now) // 토큰 생성 시간
            .setExpiration(new Date(now.getTime() + 30*60*1000L)) // 토큰 만료 시간 - 30분 후
            .signWith(SignatureAlgorithm.HS256, "mysecret") // sha256, secretkey 설정
            .compact();
        return token;
    }
}
