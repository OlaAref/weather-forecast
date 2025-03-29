package com.olaaref.weather.aop.logger.advice.after;

import com.olaaref.weather.aop.logger.enums.Level;
import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import com.olaaref.weather.aop.logger.template.interpolation.dto.ReturnValueInfo;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.ExceptionStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.JoinPointStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.ReturnValueStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;
import com.olaaref.weather.aop.logger.util.LoggerUtil;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static com.olaaref.weather.aop.logger.util.LoggerUtil.isExceptionIgnored;

public class LogAfterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAfterService.class);

    @Autowired
    private StringSubstitutor stringSubstitutor;

    @Autowired
    private JoinPointStringSupplierRegistrar joinPointStringSupplierRegistrar;

    @Autowired
    private ReturnValueStringSupplierRegistrar returnValueStringSupplierRegistrar;

    @Autowired
    private ExceptionStringSupplierRegistrar exceptionStringSupplierRegistrar;

    private final AopLoggersProperties aopLoggersProperties;

    public LogAfterService(AopLoggersProperties aopLoggersProperties) {
        this.aopLoggersProperties = aopLoggersProperties;
    }

    public void logAfter(JoinPoint joinPoint, LogAfter logAfterAnnotation, Object returnValue, Throwable exception) {
        long startTime = System.nanoTime();

        boolean isAfterThrowing = exception != null;

        Level level = getLoggingLevel(logAfterAnnotation.level(), isAfterThrowing);
        Logger logger = LoggerUtil.getLogger(logAfterAnnotation.declaringClass(), joinPoint);

        if(isLoggingLevelDisabled(logger, level)
                || isExceptionIgnored(exception, logAfterAnnotation.ignoreExceptions(), aopLoggersProperties)) {
            logElapsed(startTime);
            return;
        }

        StringSupplierLookup stringLookup = new StringSupplierLookup();

        logMessage(joinPoint, level, logAfterAnnotation, logger, stringLookup, returnValue, exception, isAfterThrowing);
        logElapsed(startTime);
    }

    private void logMessage(
            JoinPoint joinPoint,
            Level level,
            LogAfter logAfterAnnotation,
            Logger logger,
            StringSupplierLookup stringLookup,
            Object returnValue,
            Throwable exception,
            boolean isAfterThrowing
    ) {
        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        joinPointStringSupplierRegistrar.register(stringLookup, joinPoint);
        String annotationMessage;
        if(isAfterThrowing){
            exceptionStringSupplierRegistrar.register(stringLookup, exception);
            annotationMessage = logAfterAnnotation.exitedAbnormallyMessage();
        } else {
            returnValueStringSupplierRegistrar.register(stringLookup, new ReturnValueInfo(joinPoint, returnValue));
            annotationMessage = logAfterAnnotation.exitedMessage();
        }
        String message = stringSubstitutor.substitute(getMessageTemplate(annotationMessage, isAfterThrowing), stringLookup);

        LoggerUtil.log(logger, level, message);

    }

    private String getMessageTemplate(String annotationMessage, boolean isAfterThrowing) {
        if(!annotationMessage.isEmpty()) return annotationMessage;
        return isAfterThrowing ? aopLoggersProperties.getExitedAbnormallyMessage() : aopLoggersProperties.getExitedMessage();
    }

    private void logElapsed(long startTime) {
        LOGGER.debug("[logAfter] elapsed [{}]", Duration.ofNanos(System.nanoTime() - startTime));
    }

    private boolean isLoggingLevelDisabled(Logger logger, Level level) {
        return !LoggerUtil.isEnabled(logger, level);
    }

    private Level getLoggingLevel(Level annotationLevel, boolean isAfterThrowing) {
        if(annotationLevel != Level.DEFAULT) return annotationLevel;
        return isAfterThrowing ? aopLoggersProperties.getExitedAbnormallyLevel() : aopLoggersProperties.getExitedLevel();
    }
}
