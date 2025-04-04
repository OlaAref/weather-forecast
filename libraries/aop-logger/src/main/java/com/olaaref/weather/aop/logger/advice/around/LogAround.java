package com.olaaref.weather.aop.logger.advice.around;

import com.olaaref.weather.aop.logger.enums.Level;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface LogAround {
    /**
     * @return Class name used as Logger's category name
     */
    Class<?> declaringClass() default void.class;

    /**
     * @return Log Level for entering, exited normally and elapsed message
     */
    Level level() default Level.DEFAULT;

    /**
     * @return Entering message template
     */
    String enteringMessage() default "";

    /**
     * @return Exited message template
     */
    String exitedMessage() default "";

    /**
     * @return Log level for exited abnormally message
     */
    Level exitedAbnormallyLevel() default Level.DEFAULT;

    /**
     * @return Exited abnormally message template
     */
    String exitedAbnormallyMessage() default "";

    /**
     * @return Exceptions that will be ignored by Logger
     */
    Class<? extends Throwable>[] ignoreExceptions() default {};

    /**
     * @return Whether to print exception and its backtrace
     */
    boolean printStackTrace() default true;

    /**
     * @return Elapsed message template
     */
    String elapsedMessage() default "";

    /**
     * @return Log level for elapsed warning message
     */
    Level elapsedWarningLevel() default Level.DEFAULT;

    /**
     * @return Elapsed warning message template
     */
    String elapsedWarningMessage() default "";

    /**
     * @return Elapsed time limit to log elapsed warning message
     */
    long elapsedTimeLimit() default 0;

    /**
     * @return Elapsed time unit
     */
    ChronoUnit elapsedTimeUnit() default ChronoUnit.MILLIS;
}
