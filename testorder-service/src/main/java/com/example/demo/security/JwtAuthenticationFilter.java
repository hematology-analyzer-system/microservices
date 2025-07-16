package com.example.demo.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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


        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        try{
            Claims claims = jwtService.getClaimsFromToken(token);

            String username = claims.getSubject(); // sub
            Long userId = claims.get("userid", Long.class);
            String fullName = claims.get("fullname", String.class);
            String identifyNum = claims.get("identifyNum", String.class);

//            List<GrantedAuthority> authorities
//                    = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            CurrentUser currentUser = new CurrentUser(userId, username, fullName, identifyNum);

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
