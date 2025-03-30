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

/**
 * Service class responsible for handling logging around method execution in the AOP logging framework.
 * <p>
 * This service processes {@link LogAround} annotations and generates appropriate log messages
 * at different stages of method execution:
 * <ul>
 *   <li>Before method execution (entering)</li>
 *   <li>After successful method execution (exited)</li>
 *   <li>After method execution with exception (exited abnormally)</li>
 *   <li>Elapsed time for method execution</li>
 * </ul>
 * <p>
 * The service integrates with various string supplier registrars to populate context information
 * in log messages, such as method details, arguments, return values, exceptions, and execution time.
 * <p>
 * Unlike {@code LogBeforeService} and {@code LogAfterService}, this service uses a {@link ProceedingJoinPoint}
 * to control the execution flow of the intercepted method, allowing for comprehensive logging
 * throughout the entire method execution lifecycle.
 *
 * @see LogAround
 * @see com.olaaref.weather.aop.logger.util.LoggerUtil
 * @see com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor
 */
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

    /**
     * Constructs a new LogAroundService with the specified properties.
     *
     * @param aopLoggersProperties The properties containing default configuration for AOP logging
     */
    public LogAroundService(AopLoggersProperties aopLoggersProperties) {
        this.aopLoggersProperties = aopLoggersProperties;
    }

    /**
     * Main method that handles logging around method execution based on the {@link LogAround} annotation.
     * <p>
     * This method implements the complete around advice lifecycle:
     * <ol>
     *   <li>Logs the entering message before method execution</li>
     *   <li>Executes the intercepted method</li>
     *   <li>Logs the exited message after successful method execution</li>
     *   <li>Logs the elapsed time for method execution</li>
     *   <li>Logs the exited abnormally message if an exception occurs</li>
     *   <li>Re-throws any exceptions to maintain the original method behavior</li>
     * </ol>
     *
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method
     * @param logAround The LogAround annotation containing logging configuration
     * @return The value returned by the intercepted method
     * @throws Throwable Any exception thrown by the intercepted method
     */
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

    /**
     * Logs the entering message before method execution.
     * <p>
     * This method is called before the intercepted method is executed. It registers
     * the necessary string suppliers for the join point and generates the entering message
     * using the appropriate template. If the method is configured to be ignored or if
     * logging is disabled for the determined level, no logging will occur.
     *
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method
     * @param logAround The LogAround annotation containing logging configuration
     * @param logger The logger to use for logging
     * @param stringLookup The string lookup for variable interpolation
     */
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

    /**
     * Logs the exited message after successful method execution.
     * <p>
     * This method is called after the intercepted method has executed successfully.
     * It registers the necessary string suppliers for the return value and generates
     * the exited message using the appropriate template. If the method is configured
     * to be ignored or if logging is disabled for the determined level, no logging will occur.
     *
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method
     * @param logAround The LogAround annotation containing logging configuration
     * @param logger The logger to use for logging
     * @param stringLookup The string lookup for variable interpolation
     * @param returnValue The value returned by the intercepted method
     */
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

    /**
     * Logs the exited abnormally message when the method throws an exception.
     * <p>
     * This method is called when the intercepted method has thrown an exception.
     * It registers the necessary string suppliers for the exception and generates
     * the exited abnormally message using the appropriate template. If the method is
     * configured to be ignored, if logging is disabled for the determined level, or if
     * the exception is configured to be ignored, no logging will occur.
     * <p>
     * If the printStackTrace flag is set in the annotation, the exception stack trace
     * will be included in the log message.
     *
     * @param logAround The LogAround annotation containing logging configuration
     * @param logger The logger to use for logging
     * @param stringLookup The string lookup for variable interpolation
     * @param exception The exception thrown by the intercepted method
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method
     */
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

    /**
     * Logs the elapsed time for method execution.
     * <p>
     * This method is called after the intercepted method has executed (either successfully
     * or with an exception). It registers the necessary string suppliers for the elapsed time
     * and generates the elapsed time message using the appropriate template. If the method is
     * configured to be ignored or if logging is disabled for the determined level, no logging
     * will occur.
     *
     * @param logAround The LogAround annotation containing logging configuration
     * @param logger The logger to use for logging
     * @param stringLookup The string lookup for variable interpolation
     * @param endTime The elapsed time in nanoseconds
     * @param joinPoint The ProceedingJoinPoint representing the intercepted method
     */
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

    /**
     * Determines the message template to use based on annotation and properties.
     * <p>
     * If the annotation provides a non-empty template, it will be used. Otherwise,
     * the default template from properties will be used.
     *
     * @param annotationTemplate The template specified in the annotation
     * @param propertiesTemplate The default template from properties
     * @return The message template to use
     */
    private String getMessageTemplate(String annotationTemplate, String propertiesTemplate) {
        return annotationTemplate.isEmpty() ? propertiesTemplate : annotationTemplate;
    }

    /**
     * Checks if logging is disabled for the specified level.
     *
     * @param logger The logger to check
     * @param annotationLevel The logging level to check
     * @return true if logging is disabled, false otherwise
     */
    private boolean isLoggingLevelDisabled(Logger logger, Level annotationLevel) {
        return !LoggerUtil.isEnabled(logger, annotationLevel);
    }

    /**
     * Determines the appropriate logging level based on annotation.
     * <p>
     * If the annotation specifies a non-default level, it will be used. Otherwise,
     * the default entering level from properties will be used.
     *
     * @param annotationLevel The level specified in the annotation
     * @return The logging level to use
     */
    private Level getLoggingLevel(Level annotationLevel) {
        return annotationLevel == Level.DEFAULT ? aopLoggersProperties.getEnteringLevel() : annotationLevel;
    }
}
