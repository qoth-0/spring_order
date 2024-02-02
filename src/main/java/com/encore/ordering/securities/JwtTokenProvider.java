package com.encore.ordering.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    //    Value : yml에 있는 정보를 프로그램 안으로 가져오는 어노테이션
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private int expiration;

    public String createToken(String email, String role) {
//        claims : 토큰 사용자에 대한 속성이나 데이터 포함 - 주로 페이로드를 의미
        Claims claims = Jwts.claims().setSubject(email); // Subject : pk(email)
        log.debug("secretKey : " + secretKey);
        log.debug("expiration : " + expiration);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now) // 토큰 생성 시간
            .setExpiration(new Date(now.getTime() + expiration*60*1000L)) // 토큰 만료 시간 - 30분 후
            .signWith(SignatureAlgorithm.HS256, secretKey) // sha256, secretkey 설정
            .compact();
        return token;
    }
}
