package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * {@link ToStringConverter} implementation for {@link Optional} types, using
 * {@link ObjectToStringConverter} to return a String representation of the contained element.
 *
 * <p>This strategy handles Java's Optional container type and properly manages empty Optionals
 * by representing them as the string "null". For non-empty Optionals, it delegates to {@link ObjectToStringConverter}
 * to generate appropriate string representations of the contained value.</p>
 *
 * <p>Unlike other collection-based strategies, this implementation does not enclose the result in brackets,
 * as Optional is treated as a single value container rather than a collection.</p>
 *
 * @see ToStringConverter
 * @see ObjectToStringConverter
 * @see Optional
 */
public class OptionalToStringConverter implements ToStringConverter {

    @Autowired
    private ObjectToStringConverter objectToStringStrategy;

    /**
     * Determines if the given object type is an Optional.
     *
     * <p>This implementation checks if the object is an instance of {@link Optional}.</p>
     *
     * @param object the object to check support for
     * @return true if the object is an Optional, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof Optional<?>;
    }

    /**
     * Converts the given Optional object to its string representation.
     *
     * <p>This method checks if the Optional is empty. If empty, it returns "null".
     * Otherwise, it extracts the contained value and delegates to {@link ObjectToStringConverter}
     * to generate the string representation.</p>
     *
     * @param object the Optional object to convert to string
     * @return the string representation of the Optional's value, or "null" if empty
     */
    @Override
    public String toString(Object object) {
        Optional<?> optionalObject = (Optional<?>) object;
        return optionalObject.isEmpty() ? "null" : objectToStringStrategy.toString(optionalObject.get());
    }
}
