package com.olaaref.weather.aop.logger.advice.after;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class LogAfterAdvice {
    @Autowired
    private LogAfterService logAfterService;

    @Pointcut("execution(public * *(..))")
    void publicMethod() {
    }

    @Pointcut("execution(String *.toString())")
    void toStringMethod() {
    }

    @Pointcut(value = "@annotation(logAfter)", argNames = "logAfter")
    void logAfterMethodContext(LogAfter logAfter) {
    }

    @Pointcut(value = "@within(logAfter)", argNames = "logAfter")
    void logAfterClassContext(LogAfter logAfter) {
    }

    @AfterReturning(
            value = "publicMethod() && logAfterMethodContext(logAfter)",
            argNames = "joinPoint, logAfter, returnValue",
            returning = "returnValue")
    void logAfterReturningMethodContext(
            final JoinPoint joinPoint,
            final LogAfter logAfter,
            final Object returnValue
    ) {
        logAfter(joinPoint, logAfter, returnValue, null);
    }

    @AfterReturning(
            value = "publicMethod() && !toStringMethod() && logAfterClassContext(logAfter)",
            argNames = "joinPoint, logAfter, returnValue",
            returning = "returnValue"
    )
    void logAfterReturningClassContext(
            final JoinPoint joinPoint,
            final LogAfter logAfter,
            final Object returnValue
    ) {
        logAfter(joinPoint, logAfter, returnValue, null);
    }

    @AfterThrowing(
            value = "publicMethod() && !toStringMethod() && logAfterMethodContext(logAfter)",
            argNames = "joinPoint, logAfter, exception",
            throwing = "exception"
    )
    void logAfterThrowingMethodContext(
            final JoinPoint joinPoint,
            final LogAfter logAfter,
            final Throwable exception
    ) {
        logAfter(joinPoint, logAfter, null, exception);
    }

    @AfterThrowing(
            value = "publicMethod() && !toStringMethod() && logAfterClassContext(logAfter)",
            argNames = "joinPoint, logAfter, exception",
            throwing = "exception"
    )
    void logAfterThrowingClassContext(
            final JoinPoint joinPoint,
            final LogAfter logAfter,
            final Throwable exception
    ) {
        logAfter(joinPoint, logAfter, null, exception);
    }

    protected void logAfter(JoinPoint joinPoint, LogAfter logAfter, Object returnValue, Throwable exception) {
        logAfterService.logAfter(joinPoint, logAfter, returnValue, exception);
    }
}
