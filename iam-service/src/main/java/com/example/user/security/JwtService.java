package com.example.user.security;

import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {

    private static final String SECRET_KEY = "my-super-ultra-promax-vip-pro-password-32-bytes-long";
//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
//        Map<String, Object> claims = new HashMap<>();
//
//
////        claims.put("email", user.getEmail());
//        claims.put("status", user.getStatus());
//
//
//        Set<Map<String, Object>> userRoles = new HashSet<>();
//        Set<Long> privilegeIds = new HashSet<>();
//
//        if (user.getRoles() != null) {
//            for (Role role : user.getRoles()) {
//                Map<String, Object> roleInfo = new HashMap<>();
//                roleInfo.put("id", role.getRoleId());
//                roleInfo.put("name", role.getName());
//                roleInfo.put("code", role.getCode());
//                userRoles.add(roleInfo);
//
//                if (role.getPrivileges() != null) {
//                    for (Privilege privilege : role.getPrivileges()) {
//                        privilegeIds.add(privilege.getPrivilegeId());
//                    }
//                }
//            }
//        }
//
//        claims.put("roles", userRoles);
//        claims.put("privilege_ids", privilegeIds);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userid", user.getId());
        claims.put("fullname", user.getFullName());
        claims.put("email", user.getEmail());
        claims.put("identifyNum", user.getIdentifyNum());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                // Set expiration to 1 day (86,400,000 milliseconds).
                // Consider shorter lifespans for access tokens and use refresh tokens for longer sessions.
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
//        return SECRET_KEY;
    }
}

