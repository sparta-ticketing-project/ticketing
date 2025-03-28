package com.example.ticketing.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 메소드에만 적용
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 사용
public @interface AdminOnly {
}
