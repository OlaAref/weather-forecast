package com.olaaref.weather.aop.logger.advice.around;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class LogAroundAdvice {

    @Autowired
    private LogAroundService logAroundService;

    @Pointcut("execution(public * *(..))")
    void publicMethod(){}

    @Pointcut("execution(String *.toString())")
    void toStringMethod(){}

    @Pointcut(
            value = "@annotation(LogAround)",
            argNames = "logAround"
    )
    void logAroundMethodContext(final LogAround logAround){}

    @Pointcut(
            value = "@within(LogAround)",
            argNames = "logAround"
    )
    void logAroundClassContext(final LogAround logAround){}

    @Around(
            value = "publicMethod() && logAroundMethodContext(logAround)",
            argNames = "joinPoint, logAround"
    )
    Object logAroundMethodContext(final ProceedingJoinPoint joinPoint, final LogAround logAround) throws Throwable{
        return logAround(joinPoint, logAround);
    }

    @Around(
            value = "publicMethod() && !toStringMethod() && logAroundClassContext(logAround)",
            argNames = "joinPoint, logAround"
    )
    Object logAroundClassContext(final ProceedingJoinPoint joinPoint, final LogAround logAround) throws Throwable{
        return logAround(joinPoint, logAround);
    }

    protected Object logAround(ProceedingJoinPoint joinPoint, LogAround logAround) throws Throwable {
        return logAroundService.logAround(joinPoint, logAround);
    }

}
