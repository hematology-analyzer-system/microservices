package com.example.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;

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
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            log.info("Headers:");
//            while (headerNames.hasMoreElements()) {
//                String headerName = headerNames.nextElement();
//                log.info("  {}: {}", headerName, request.getHeader(headerName));
//            }
//        }
//
//        // Define public paths that should skip JWT auth
//        String path = request.getRequestURI();
//        log.info("Path: {}", path);
//        List<String> publicPaths = List.of(
//                "/iam/auth/register",
//                "/iam/auth/login",
//                "/iam/auth/resend-otp",
//                "/iam/auth/verify-otp",
//                "/iam/auth/forgot-password",
//                "/iam/auth/verify-reset-otp",
//                "/iam/auth/reset-password",
//                "/iam/auth/logout",
//                "/iam/actuator/health"
//        );
//
//        if (publicPaths.contains(path)) {
//            log.info("Public path '{}', skipping JWT authentication", path);
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//        String jwt = null;
//
//        // Try to extract from Authorization header first
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            jwt = authHeader.substring(7);
//            log.info("JWT extracted from Authorization header.");
//        } else {
//            // Fallback: Try to extract from cookies
//            if (request.getCookies() != null) {
//                for (Cookie cookie : request.getCookies()) {
//                    if ("token".equals(cookie.getName())) {
//                        jwt = cookie.getValue();
//                        log.info("JWT extracted from cookie.");
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (jwt == null) {
//            log.info("No JWT found, continuing filter chain");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String username = jwtService.extractUsername(jwt);
//            log.info("Username extracted from token: {}", username);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
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

        String path = request.getRequestURI();
        log.info("Path: {}", path);
        List<String> publicPaths = List.of(
                "/iam/auth/register",
                "/iam/auth/login",
                "/iam/auth/resend-otp",
                "/iam/auth/verify-otp",
                "/iam/auth/forgot-password",
                "/iam/auth/verify-reset-otp",
                "/iam/auth/reset-password",
                "/iam/auth/logout",
                "/iam/actuator/health"
        );

        if (publicPaths.contains(path)) {
            log.info("Public path '{}', skipping JWT authentication", path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String authHeader = request.getHeader("Authorization");

        // 1. Check Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.info("JWT extracted from Authorization header.");
        }

        // 2. Fallback to HttpOnly cookie if no Authorization header
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    log.info("JWT extracted from HttpOnly cookie.");
                    break;
                }
            }
        }

        if (jwt == null) {
            log.info("No JWT found in header or cookie, continuing filter chain without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(jwt);
            log.info("Username extracted from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set in SecurityContext for user: {}", username);
                } else {
                    log.warn("JWT token is invalid for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }


    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else return principal.toString();
    }

}
