package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of {@link ToStringConverter} that serves as a fallback strategy for converting
 * objects to their string representation. This class acts as the final strategy in the chain of responsibility
 * pattern used by the framework's string conversion system.
 *
 * <p>This strategy has two key behaviors:</p>
 * <ul>
 *   <li>For null objects, it returns the string "null"</li>
 *   <li>For non-null objects, it first attempts to delegate to {@link ReflectionToStringConverter} if that
 *       strategy supports the object type</li>
 *   <li>If the reflection strategy doesn't support the object, it falls back to using {@link String#valueOf(Object)}</li>
 * </ul>
 *
 * <p>Since this strategy's {@link #supports(Object)} method always returns true, it serves as a catch-all
 * strategy that can handle any object type. This makes it suitable as a default strategy or as a delegate
 * for other more specialized strategies when they encounter objects outside their domain.</p>
 *
 * @see ToStringConverter
 * @see ReflectionToStringConverter
 * @see String#valueOf(Object)
 */
public class ObjectToStringConverter implements ToStringConverter {

    /**
     * The reflection-based strategy that this strategy delegates to when possible.
     * This dependency is automatically injected by Spring's dependency injection system.
     */
    @Autowired
    private ReflectionToStringConverter reflectionToStringStrategy;

    /**
     * Determines if this strategy can handle the given object type.
     *
     * <p>This implementation always returns true, making this strategy a catch-all
     * that can handle any object type, including null values. This makes the strategy
     * suitable as a default fallback when no other strategies apply.</p>
     *
     * @param object the object to check support for, can be null
     * @return always returns true, indicating this strategy can handle any object
     */
    @Override
    public boolean supports(Object object) {
        return true;
    }

    /**
     * Converts the given object to its string representation.
     *
     * <p>This method implements the following logic:</p>
     * <ol>
     *   <li>If the object is null, returns the string "null"</li>
     *   <li>If the object is not supported by the {@link ReflectionToStringConverter},
     *       uses {@link String#valueOf(Object)} to convert it</li>
     *   <li>Otherwise, delegates to the {@link ReflectionToStringConverter} for a more
     *       detailed string representation</li>
     * </ol>
     *
     * @param object the object to convert to string, can be null
     * @return the string representation of the object, or "null" if the object is null
     */
    @Override
    public String toString(Object object) {
        if(object == null || !reflectionToStringStrategy.supports(object)) {
            return String.valueOf(object);
        }
        return reflectionToStringStrategy.toString(object);
    }
}
