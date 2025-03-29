package com.olaaref.weather.aop.logger.advice.before;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class LogBeforeAdvice {

    @Autowired
    private LogBeforeService logBeforeService;

    @Pointcut("execution(public * *(..))")
    void publicMethod() {
    }

    @Pointcut("execution(String *.toString())")
    void toStringMethod() {
    }

    @Pointcut(value = "@annotation(logBefore)", argNames = "logBefore")
    void logBeforeMethodContext(LogBefore logBefore) {
    }

    @Pointcut(value = "@within(logBefore)", argNames = "logBefore")
    void logBeforeClassContext(LogBefore logBefore) {
    }

    @Before(
            value = "publicMethod() && logBeforeMethodContext(logBefore)",
            argNames = "joinPoint, logBefore")
    void logBeforeMethodContext(JoinPoint joinPoint, LogBefore logBefore) {
        logBefore(joinPoint, logBefore);
    }

    @Before(
            value = "publicMethod() && !toStringMethod() && logBeforeClassContext(logBefore)",
            argNames = "joinPoint, logBefore")
    void logBeforeClassContext(JoinPoint joinPoint, LogBefore logBefore) {
        logBefore(joinPoint, logBefore);
    }

    protected void logBefore(JoinPoint joinPoint, LogBefore logBefore) {
        logBeforeService.logBefore(joinPoint, logBefore);
    }
}

