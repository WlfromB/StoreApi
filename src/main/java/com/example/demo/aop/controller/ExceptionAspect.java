package com.example.demo.aop.controller;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionAspect {
    @AfterThrowing(pointcut = "execution(* com.example.demo.controllers..*(..))", throwing = "ex")
    public ResponseEntity<String> handleControllerException(Exception ex) {
        log.error("Exception caught in AOP: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}
