package com.example.demo.security;

import com.example.demo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;



@Component
public class JwtTokenUtil {
    private final Key key;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        byte[] keyBytes =  jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
