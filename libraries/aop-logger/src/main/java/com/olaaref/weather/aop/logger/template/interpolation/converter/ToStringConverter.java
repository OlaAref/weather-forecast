package com.olaaref.weather.aop.logger.template.interpolation.converter;

import com.olaaref.weather.aop.logger.template.interpolation.registrar.StringSupplierRegistrar;

/**
 * Strategy interface for converting objects to their string representation. This interface is used by
 * {@link StringSupplierRegistrar} implementations to provide consistent string formatting across the application.
 *
 * <p>The framework provides several implementations for common types:
 * <ul>
 *   <li>{@link ObjectToStringConverter} - Default implementation using Object.toString()</li>
 *   <li>{@link ReflectionToStringConverter} - Uses reflection to create detailed string representations</li>
 *   <li>{@link ArrayToStringConverter} - Handles array type conversion</li>
 *   <li>{@link IterableToStringConverter} - Converts Iterable types</li>
 *   <li>{@link OptionalToStringConverter} - Handles Optional values</li>
 *   <li>{@link PageToStringConverter} - Converts Spring Data Page objects</li>
 *   <li>{@link SliceToStringConverter} - Handles Spring Data Slice objects</li>
 * </ul>
 *
 */
public interface ToStringConverter {

    /**
     * Determines if this strategy can handle the given object type.
     * Does not check for null instance.
     *
     * @param object the object to check support for
     * @return true if this strategy can convert the object to string
     */
    boolean supports(Object object);

    /**
     * Converts the given object to its string representation.
     *
     * @param object the object to convert to string
     * @return the string representation of the object
     */
    String toString(Object object);
}
