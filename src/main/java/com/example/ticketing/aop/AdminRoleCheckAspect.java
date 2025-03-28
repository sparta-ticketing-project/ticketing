package com.example.ticketing.aop;

import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.dto.AuthUser;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminRoleCheckAspect {

    @Before("@annotation(AdminOnly)")
    public void checkAdminPermission(JoinPoint joinPoint) {

        // 메서드의 인자 중 AuthUser 타입을 찾아서 권한 체크
        AuthUser authUser = findAuthUserArgument(joinPoint);

        UserRole currentUserRole = authUser.getUserRole();

        if (currentUserRole == UserRole.USER) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }
    }

    private AuthUser findAuthUserArgument(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof AuthUser) {
                return (AuthUser) arg;
            }
        }
        throw new IllegalArgumentException("No AuthUser found");
    }


}
