package com.example.ticketing.global.auth;

import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (isWhiteList(httpRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String bearer = httpRequest.getHeader("Authorization");

        try {
            if (bearer == null || bearer.isEmpty()) {
                throw new CustomException(ExceptionType.REQUIRED_JWT_TOKEN);
            }

            String jwt = jwtUtil.substringToken(bearer);

            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims.isEmpty()) {
                    throw new CustomException(ExceptionType.INVALID_JWT_TOKEN);
                }

                httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
                httpRequest.setAttribute("userRole", claims.get("userRole"));

                filterChain.doFilter(servletRequest, servletResponse);
            } catch (SecurityException | MalformedJwtException ex) {
                throw new CustomException(ExceptionType.INVALID_JWT_SIGNATURE);
            } catch (ExpiredJwtException ex) {
                throw new CustomException(ExceptionType.EXPIRED_JWT_TOKEN);
            } catch (UnsupportedJwtException ex) {
                throw new CustomException(ExceptionType.UNSUPPORTED_JWT_TOKEN);
            } catch (MissingRequestHeaderException | AccessDeniedException ex) {
                throw new CustomException(ExceptionType.MISSING_JWT_TOKEN);
            }

        } catch (CustomException ex) {
            parseResponseErrorMessage(httpResponse, ex);
        }
    }

    private void parseResponseErrorMessage(HttpServletResponse httpResponse, CustomException ex) throws IOException {
        httpResponse.setStatus(ex.getHttpStatus().value());
        httpResponse.setContentType("application/json;charset=UTF-8");

        String errorBody = String.format("""
            {
                "status": "%s"
                "code": "%d",
                "message": "%s",
                "timestamp": "%s"
            }
            """, ex.getHttpStatus().name(), ex.getHttpStatus().value(), ex.getMessage(), LocalDateTime.now());

        PrintWriter writer = httpResponse.getWriter();
        writer.println(errorBody);
        writer.flush();
    }

    private boolean isWhiteList(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (!WHITE_LIST.containsKey(method)) {
            return false;
        }

        String[] lists = WHITE_LIST.get(method);
        return PatternMatchUtils.simpleMatch(lists, path);
    }
}
