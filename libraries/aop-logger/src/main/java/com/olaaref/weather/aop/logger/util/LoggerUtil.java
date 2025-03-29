package com.olaaref.weather.aop.logger.util;

import com.olaaref.weather.aop.logger.annotation.DoNotLog;
import com.olaaref.weather.aop.logger.enums.Level;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class LoggerUtil {

    public static Logger getLogger(Class<?> declaringClass, JoinPoint joinPoint) {
        Class<?> declaringClassValue =  declaringClass == null || declaringClass == void.class ? joinPoint.getSignature().getDeclaringType() : declaringClass;
        return LoggerFactory.getLogger(declaringClassValue);
    }

    public static boolean isEnabled(Logger logger, Level level) {
        return switch (level) {
            case TRACE -> logger.isTraceEnabled();
            case DEBUG -> logger.isDebugEnabled();
            case WARN -> logger.isWarnEnabled();
            case INFO -> logger.isInfoEnabled();
            case ERROR -> logger.isErrorEnabled();
            default -> false;
        };
    }

    public static void log(Logger logger, Level level, String message) {
        switch (level) {
            case TRACE:
                logger.trace(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            default:
                break;
        }
    }

    public static boolean isMethodIgnored(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return method.isAnnotationPresent(DoNotLog.class);
    }
}
