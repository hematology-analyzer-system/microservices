package com.example.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.info("JWT token extracted: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");

        try {
            username = jwtService.extractUsername(jwt);
            log.info("Username extracted from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("User details loaded: {}", userDetails.getUsername());
                log.info("User authorities: {}", userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in SecurityContext");
                } else {
                    log.warn("Token is not valid");
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
