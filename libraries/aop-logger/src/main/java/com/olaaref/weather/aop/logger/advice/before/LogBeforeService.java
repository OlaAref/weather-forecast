package com.olaaref.weather.aop.logger.advice.before;

import com.olaaref.weather.aop.logger.enums.Level;
import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.JoinPointStringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;
import com.olaaref.weather.aop.logger.util.LoggerUtil;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Objects;

/**
 * Service class responsible for handling the logging logic before method execution.
 *
 * <p>This service is used by the AOP infrastructure to log method entry events when methods
 * are annotated with {@link LogBefore}. It handles the creation and formatting of log messages
 * using string interpolation with variables from the join point context.</p>
 *
 * <p>The service supports customizable logging levels and message templates through both
 * annotation parameters and global configuration properties.</p>
 *
 * <p>Example log output:</p>
 * <pre>
 * DEBUG com.example.service.UserService : Entering [User findById(Long)] with parameters [42]
 * </pre>
 *
 * @see LogBefore
 * @see AopLoggersProperties
 */
public class LogBeforeService {

    private static Logger LOGGER = LoggerFactory.getLogger(LogBeforeService.class);

    @Autowired
    private StringSubstitutor stringSubstitutor;

    @Autowired
    private JoinPointStringSupplierRegistrar joinPointStringSupplierRegistrar;

    private final AopLoggersProperties aopLoggersProperties;

    public LogBeforeService(AopLoggersProperties aopLoggersProperties) {
        this.aopLoggersProperties = Objects.requireNonNull(aopLoggersProperties);
    }

    /**
     * Logs a message before the execution of a method annotated with {@link LogBefore}.
     *
     * <p>This method is called by the AOP infrastructure when a method annotated with
     * {@link LogBefore} is invoked. It performs the following steps:</p>
     * <ol>
     *   <li>Measures the start time for performance tracking</li>
     *   <li>Determines the appropriate logger based on the annotation's declaringClass</li>
     *   <li>Determines the logging level based on the annotation's level</li>
     *   <li>Checks if logging is enabled for the determined level</li>
     *   <li>If enabled, creates and logs the entering message with interpolated variables</li>
     *   <li>Logs the elapsed time for this logging operation at debug level</li>
     * </ol>
     *
     * @param joinPoint The AspectJ join point representing the intercepted method call
     * @param annotation The LogBefore annotation instance from the intercepted method
     */
    public void logBefore(JoinPoint joinPoint, LogBefore annotation){
        long startTime = System.nanoTime();

        Level loggingLevel = getLoggingLevel(annotation.level());
        Logger logger = LoggerUtil.getLogger(annotation.declaringClass(), joinPoint);

        if (isLoggingLevelDisabled(logger, loggingLevel)) {
            logElapsed(startTime);
            return;
        }

        StringSupplierLookup stringLookup = new StringSupplierLookup();

        logMessage(joinPoint, loggingLevel, annotation.enteringMessageTemplate(), logger, stringLookup);
        logElapsed(startTime);
    }

    /**
     * Determines the effective logging level to use.
     *
     * <p>If the specified logging level is {@link Level#DEFAULT}, this method returns
     * the default entering level from the configuration properties. Otherwise, it returns
     * the specified logging level.</p>
     *
     * @param loggingLevel The logging level specified in the annotation
     * @return The effective logging level to use
     */
    private Level getLoggingLevel(Level loggingLevel) {
        return loggingLevel == Level.DEFAULT ? aopLoggersProperties.getEnteringLevel() : loggingLevel;
    }

    /**
     * Checks if the specified logging level is disabled for the given logger.
     *
     * <p>This method uses {@link LoggerUtil#isEnabled} to determine if logging
     * is enabled for the specified level and logger, then returns the negated result.</p>
     *
     * @param logger The SLF4J logger to check
     * @param loggingLevel The logging level to check
     * @return true if logging is disabled for the specified level, false otherwise
     */
    private boolean isLoggingLevelDisabled(Logger logger, Level loggingLevel) {
        return !LoggerUtil.isEnabled(logger, loggingLevel);
    }

    /**
     * Logs the elapsed time for the logging operation at debug level.
     *
     * <p>This method calculates the time elapsed since the startTime and logs it
     * for performance monitoring purposes.</p>
     *
     * @param startTime The start time in nanoseconds
     */
    private void logElapsed(long startTime) {
        LOGGER.debug("[logBefore] elapsed [{}]", Duration.ofNanos(System.nanoTime() - startTime));
    }

    /**
     * Performs the actual logging of the message with interpolated variables.
     *
     * <p>This method handles the following steps:</p>
     * <ol>
     *   <li>Registers join point variables (like method name and parameters) with the string lookup</li>
     *   <li>Substitutes variables in the message template using the string substitutor</li>
     *   <li>Logs the resulting message at the specified level</li>
     * </ol>
     *
     * @param joinPoint The AspectJ join point representing the intercepted method call
     * @param loggingLevel The level at which to log the message
     * @param messageTemplate The message template from the annotation (may be empty)
     * @param logger The SLF4J logger to use for logging
     * @param stringLookup The lookup object for variable substitution
     */
    private void logMessage(
            JoinPoint joinPoint,
            Level loggingLevel,
            String messageTemplate,
            Logger logger,
            StringSupplierLookup stringLookup) {

        if(LoggerUtil.isMethodIgnored(joinPoint)) return;

        joinPointStringSupplierRegistrar.register(stringLookup, joinPoint);
        String message = stringSubstitutor.substitute(getMessageTemplate(messageTemplate), stringLookup);

        LoggerUtil.log(logger, loggingLevel, message);
    }

    /**
     * Determines the effective message template to use.
     *
     * <p>If the specified message template is empty, this method returns the default
     * entering message template from the configuration properties. Otherwise, it returns
     * the specified message template.</p>
     *
     * @param messageTemplate The message template specified in the annotation
     * @return The effective message template to use
     */
    private String getMessageTemplate(String messageTemplate) {
        return messageTemplate.isEmpty() ? aopLoggersProperties.getEnteringMessage() : messageTemplate;
    }


}
