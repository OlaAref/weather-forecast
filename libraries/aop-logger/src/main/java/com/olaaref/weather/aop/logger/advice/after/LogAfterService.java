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

/**
 * Service class responsible for handling logging after method execution in the AOP logging framework.
 * <p>
 * This service processes {@link LogAfter} annotations and generates appropriate log messages
 * based on the method execution result (normal return or exception thrown). It supports
 * customizable log messages through templates and variable interpolation.
 * <p>
 * The service integrates with various string supplier registrars to populate context information
 * in log messages, such as method details, return values, and exception information.
 *
 * @see LogAfter
 * @see com.olaaref.weather.aop.logger.util.LoggerUtil
 * @see com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor
 */
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

    /**
     * Constructs a new LogAfterService with the specified properties.
     *
     * @param aopLoggersProperties The properties containing default configuration for AOP logging
     */
    public LogAfterService(AopLoggersProperties aopLoggersProperties) {
        this.aopLoggersProperties = aopLoggersProperties;
    }

    /**
     * Logs information after a method has been executed, based on the {@link LogAfter} annotation.
     * <p>
     * This method handles both normal method execution (with a return value) and exceptional
     * method execution (with an exception). It determines the appropriate logging level and
     * message template based on the execution outcome and annotation settings.
     * <p>
     * If logging is disabled for the determined level or if the exception is configured to be
     * ignored, only the elapsed time will be logged at debug level.
     *
     * @param joinPoint The JoinPoint representing the intercepted method
     * @param logAfterAnnotation The LogAfter annotation containing logging configuration
     * @param returnValue The value returned by the method (null if an exception was thrown)
     * @param exception The exception thrown by the method (null if method completed normally)
     */
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

    /**
     * Logs the appropriate message based on method execution outcome.
     * <p>
     * This method registers the necessary string suppliers based on the execution context
     * (normal return or exception) and generates the log message using the appropriate template.
     * If the method is configured to be ignored, no logging will occur.
     *
     * @param joinPoint The JoinPoint representing the intercepted method
     * @param level The logging level to use
     * @param logAfterAnnotation The LogAfter annotation containing logging configuration
     * @param logger The logger to use for logging
     * @param stringLookup The string lookup for variable interpolation
     * @param returnValue The value returned by the method (null if an exception was thrown)
     * @param exception The exception thrown by the method (null if method completed normally)
     * @param isAfterThrowing Flag indicating whether the method threw an exception
     */
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

    /**
     * Determines the message template to use based on annotation and execution context.
     * <p>
     * If the annotation provides a non-empty message, it will be used. Otherwise,
     * the default message from properties will be used based on whether an exception was thrown.
     *
     * @param annotationMessage The message specified in the annotation
     * @param isAfterThrowing Flag indicating whether the method threw an exception
     * @return The message template to use
     */
    private String getMessageTemplate(String annotationMessage, boolean isAfterThrowing) {
        if(!annotationMessage.isEmpty()) return annotationMessage;
        return isAfterThrowing ? aopLoggersProperties.getExitedAbnormallyMessage() : aopLoggersProperties.getExitedMessage();
    }

    /**
     * Logs the elapsed time for the logging operation itself at debug level.
     * <p>
     * This is used to track the performance impact of the logging framework.
     *
     * @param startTime The start time in nanoseconds
     */
    private void logElapsed(long startTime) {
        LOGGER.debug("[logAfter] elapsed [{}]", Duration.ofNanos(System.nanoTime() - startTime));
    }

    /**
     * Checks if logging is disabled for the specified level.
     *
     * @param logger The logger to check
     * @param level The logging level to check
     * @return true if logging is disabled, false otherwise
     */
    private boolean isLoggingLevelDisabled(Logger logger, Level level) {
        return !LoggerUtil.isEnabled(logger, level);
    }

    /**
     * Determines the appropriate logging level based on annotation and execution context.
     * <p>
     * If the annotation specifies a non-default level, it will be used. Otherwise,
     * the default level from properties will be used based on whether an exception was thrown.
     *
     * @param annotationLevel The level specified in the annotation
     * @param isAfterThrowing Flag indicating whether the method threw an exception
     * @return The logging level to use
     */
    private Level getLoggingLevel(Level annotationLevel, boolean isAfterThrowing) {
        if(annotationLevel != Level.DEFAULT) return annotationLevel;
        return isAfterThrowing ? aopLoggersProperties.getExitedAbnormallyLevel() : aopLoggersProperties.getExitedLevel();
    }
}
