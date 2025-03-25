package com.example.ticketing.global.auth;

import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtFilter implements Filter {
    private static final Map<String, String[]> WHITE_LIST = Map.of(
            "POST", new String[]{
                    "/api/v1/auth/signup",
                    "/api/v1/auth/login"
            }
    );

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }
}
