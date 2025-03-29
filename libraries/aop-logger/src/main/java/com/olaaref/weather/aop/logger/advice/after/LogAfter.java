package com.olaaref.weather.aop.logger.advice.after;

import com.olaaref.weather.aop.logger.enums.Level;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface LogAfter {
    /**
     * @return Class name used as Logger's category name
     */
    Class<?> declaringClass() default void.class;

    /**
     * @return Log Level for exited message
     */
    Level level() default Level.DEFAULT;

    /**
     * @return Exited message template
     */
    String exitedMessage() default "";

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
}
