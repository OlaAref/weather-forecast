package com.olaaref.weather.aop.logger.advice.around;

import com.olaaref.weather.aop.logger.enums.Level;
import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import com.olaaref.weather.aop.logger.template.interpolation.dto.ReturnValueInfo;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.ElapsedStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.ExceptionStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.JoinPointStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.ReturnValueStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;
import com.olaaref.weather.aop.logger.util.LoggerUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import static com.olaaref.weather.aop.logger.util.LoggerUtil.isExceptionIgnored;

public class LogAroundService {

    @Autowired
    private StringSubstitutor stringSubstitutor;

    @Autowired
    private JoinPointStringSupplierRegistrar joinPointStringSupplierRegistrar;

    @Autowired
    private ReturnValueStringSupplierRegistrar returnValueStringSupplierRegistrar;

    @Autowired
    private ExceptionStringSupplierRegistrar exceptionStringSupplierRegistrar;

    @Autowired
    private ElapsedStringSupplierRegistrar elapsedStringSupplierRegistrar;

    private final AopLoggersProperties aopLoggersProperties;

    public LogAroundService(AopLoggersProperties aopLoggersProperties) {
        this.aopLoggersProperties = aopLoggersProperties;
    }


    public Object logAround(ProceedingJoinPoint joinPoint, LogAround logAround)  throws Throwable {

        Logger logger = LoggerUtil.getLogger(logAround.declaringClass(), joinPoint);
        StringSupplierLookup stringLookup = new StringSupplierLookup();

        logEnteringMessage(joinPoint, logAround, logger, stringLookup);
        long proceedStartTime = System.nanoTime();

        try {
            Object returnValue = joinPoint.proceed();
            long proceedEndTime = System.nanoTime() - proceedStartTime;

            logExitedMessage(joinPoint, logAround, logger, stringLookup, returnValue);
            logElapsedTime(logAround, logger, stringLookup, proceedEndTime, joinPoint);

            return returnValue;
        } catch (Throwable exception) {
            long proceedEndTime = System.nanoTime() - proceedStartTime;

            logExitedAbnoramllyMessage(logAround, logger, stringLookup, exception, joinPoint);
            logElapsedTime(logAround, logger, stringLookup, proceedEndTime, joinPoint);

            throw exception;
        }

    }

    private void logEnteringMessage(
            ProceedingJoinPoint joinPoint,
            LogAround logAround,
            Logger logger,
            StringSupplierLookup stringLookup
    ) {
        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        Level loggingLevel = getLoggingLevel(logAround.level());
        if(isLoggingLevelDisabled(logger, loggingLevel)) return;

        joinPointStringSupplierRegistrar.register(stringLookup, joinPoint);
        String enteringMessage = stringSubstitutor.substitute(
                getMessageTemplate(logAround.enteringMessage(), aopLoggersProperties.getEnteringMessage()),
                stringLookup
        );

        LoggerUtil.log(logger, loggingLevel, enteringMessage);
    }

    private void logExitedMessage(
            ProceedingJoinPoint joinPoint,
            LogAround logAround,
            Logger logger,
            StringSupplierLookup stringLookup,
            Object returnValue
    ) {
        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        Level loggingLevel = getLoggingLevel(logAround.level());
        if(isLoggingLevelDisabled(logger, loggingLevel)) return;

        returnValueStringSupplierRegistrar.register(stringLookup, new ReturnValueInfo(joinPoint, returnValue));
        String exitedMessage = stringSubstitutor.substitute(
                getMessageTemplate(logAround.exitedMessage(), aopLoggersProperties.getExitedMessage()),
                stringLookup
        );

        LoggerUtil.log(logger, loggingLevel, exitedMessage);
    }

    private void logExitedAbnoramllyMessage(
            LogAround logAround,
            Logger logger,
            StringSupplierLookup stringLookup,
            Throwable exception,
            ProceedingJoinPoint joinPoint
    ) {
        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        Level loggingLevel = getLoggingLevel(logAround.exitedAbnormallyLevel());
        if(isLoggingLevelDisabled(logger, loggingLevel)
                || isExceptionIgnored(exception, logAround.ignoreExceptions(), aopLoggersProperties)) return;

        exceptionStringSupplierRegistrar.register(stringLookup, exception);
        String exitedAbnormallyMessage = stringSubstitutor.substitute(
                getMessageTemplate(logAround.exitedAbnormallyMessage(), aopLoggersProperties.getExitedAbnormallyMessage()),
                stringLookup
        );

        if(logAround.printStackTrace()) {
            LoggerUtil.logException(logger, loggingLevel, exitedAbnormallyMessage, exception);
        } else {
            LoggerUtil.log(logger, loggingLevel, exitedAbnormallyMessage);
        }
    }

    private void logElapsedTime(
            LogAround logAround,
            Logger logger,
            StringSupplierLookup stringLookup,
            long endTime,
            ProceedingJoinPoint joinPoint) {
        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        Level loggingLevel = getLoggingLevel(logAround.level());
        if(isLoggingLevelDisabled(logger, loggingLevel)) return;

        elapsedStringSupplierRegistrar.register(stringLookup, endTime);
        String elapsedTimeMessage = stringSubstitutor.substitute(
                getMessageTemplate(logAround.elapsedMessage(), aopLoggersProperties.getElapsedMessage()),
                stringLookup
        );

        LoggerUtil.log(logger, loggingLevel, elapsedTimeMessage);
    }

    private String getMessageTemplate(String annotationTemplate, String propertiesTemplate) {
        return annotationTemplate.isEmpty() ? propertiesTemplate : annotationTemplate;
    }

    private boolean isLoggingLevelDisabled(Logger logger, Level annotationLevel) {
        return !LoggerUtil.isEnabled(logger, annotationLevel);
    }

    private Level getLoggingLevel(Level annotationLevel) {
        return annotationLevel == Level.DEFAULT ? aopLoggersProperties.getEnteringLevel() : annotationLevel;
    }
}
