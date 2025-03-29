package com.olaaref.weather.aop.logger.advice.before;

import com.olaaref.weather.aop.logger.enums.Level;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface LogBefore {
    /**
     * @return Class name used as Logger's category name
     */
    Class<?> declaringClass() default void.class;

    /**
     * @return Log Level for entering message template
     */
    Level level() default Level.DEFAULT;

    /**
     * @return Entering message template
     */
    String enteringMessageTemplate() default "";
}
