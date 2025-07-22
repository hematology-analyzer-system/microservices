package com.example.demo.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.List;
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwt) {
        this.jwtService = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String jwt = null;

        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            log.info("JWT extracted from Authorization header.");
        } else {
            // Fallback: Try to extract from cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        log.info("JWT extracted from cookie.");
                        break;
                    }
                }
            }
        }


        final String token = jwt;

        try{
            Claims claims = jwtService.getClaimsFromToken(token);

            String username = claims.getSubject(); // sub
            Long userId = claims.get("userid", Long.class);
            String fullname = claims.get("fullname", String.class);
            String email = String.valueOf(claims.get("email", String.class));
            String identifyNum = claims.get("identifyNum", String.class);

//            List<GrantedAuthority> authorities
//                    = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            CurrentUser currentUser = CurrentUser.builder()
                    .userId(userId)
                    .fullname(fullname)
                    .email(email)
                    .identifyNum(identifyNum)
                    .build();

            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(username, null, null);

            authentication.setDetails(currentUser);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (JwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
