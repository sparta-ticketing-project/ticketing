package com.example.ticketing.global.auth;

import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class UserRoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object roleAttr = request.getAttribute("userRole");

        if (roleAttr == null) {
            throw new CustomException(ExceptionType.REQUIRED_JWT_TOKEN);
        }

        UserRole userRole = UserRole.of(roleAttr.toString());
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/v1/admin") && !userRole.equals(UserRole.ADMIN)) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }

        return true;
    }
}
