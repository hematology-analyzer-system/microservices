package com.example.user.security;

import com.example.user.dto.userdto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Collections;
import java.util.Enumeration;

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

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        log.info("----- Incoming HTTP Request -----");
//        log.info("Method: {}", request.getMethod());
//        log.info("URL: {}", request.getRequestURL());
//        log.info("Query: {}", request.getQueryString());
//
//        log.info("Headers:");
//        Collections.list(request.getHeaderNames()).forEach(headerName -> {
//            String headerValue = request.getHeader(headerName);
//            log.info("  {}: {}", headerName, headerValue);
//        });
//
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String username;
//
//        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
//        log.info("Authorization header: {}", authHeader);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            log.info("No Bearer token found, continuing filter chain");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//        log.info("JWT token extracted: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
//
//        try {
//            username = jwtService.extractUsername(jwt);
//            log.info("Username extracted from token: {}", username);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                log.info("User details loaded: {}", userDetails.getUsername());
//                log.info("User authorities: {}", userDetails.getAuthorities());
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                    log.info("Authentication set in SecurityContext");
//                } else {
//                    log.warn("Token is not valid");
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error processing JWT token: {}", e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("----- Incoming HTTP Request -----");
        log.info("Method: {}", request.getMethod());
        log.info("URL: {}", request.getRequestURL());
        log.info("Query: {}", request.getQueryString());

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            log.info("Headers:");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.info("  {}: {}", headerName, request.getHeader(headerName));
            }
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        // Try to extract from Authorization header first
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
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

        if (jwt == null) {
            log.info("No JWT found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(jwt);
            log.info("Username extracted from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in SecurityContext for {}", userDetails.getEmail());
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
