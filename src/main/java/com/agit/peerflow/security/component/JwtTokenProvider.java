package com.agit.peerflow.security.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long validityInMilliseconds = 1000L * 60 * 60; // 1시간

    public JwtTokenProvider() {
        String base64Secret = "u8nFz9J3Q1y7m4aVt0pX9qL2s5Yh8BvCj3kR6nW0zPqU1xY2rT5vZ8mN1oQ4wE7";
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    // 토큰 생성 시, email을 subject로 설정
    public String createToken(String email, Map<String, Object> extraClaims) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        Claims claims = Jwts.claims()
                .subject(email) // <<-- 이 부분이 수정되었습니다.
                .add(extraClaims)
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 email 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}