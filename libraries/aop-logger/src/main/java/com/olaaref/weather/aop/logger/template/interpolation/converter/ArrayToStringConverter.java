package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;

/**
 * {@link ToStringConverter} implementation for array types, using {@link ObjectToStringConverter} to return
 * a String representation of each element. This strategy converts arrays to a comma-separated list of elements
 * enclosed within square brackets "[]".
 *
 * <p>This strategy handles all array types (primitive and object arrays) and properly manages null elements
 * by representing them as the string "null". For non-null elements, it delegates to {@link ObjectToStringConverter}
 * to generate appropriate string representations.</p>
 *
 * @see ToStringConverter
 * @see ObjectToStringConverter
 */
public class ArrayToStringConverter implements ToStringConverter {

    @Autowired
    private ObjectToStringConverter objectToStringStrategy;

    /**
     * Determines if the given object type is an array.
     *
     * <p>This implementation checks if the object is non-null and is an array type.
     * It supports all array types including primitive arrays and object arrays.</p>
     *
     * @param object the object to check support for
     * @return true if the object is a non-null array, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object != null && object.getClass().isArray();
    }


    /**
     * Converts the given array object to its string representation.
     *
     * <p>This method uses java.lang.reflect.Array to handle all array types including primitive arrays.
     * <p>It iterates through the array elements, appending each element's string
     * representation to a StringBuilder. Elements are separated by commas and the entire list
     * is enclosed in square brackets. For empty arrays, it returns "[]".</p>
     *
     * @param object the array object to convert to string
     * @return the string representation of the array
     */
    @Override
    public String toString(Object object) {
        final int length = Array.getLength(object);
        
        if (length == 0) {
            return "[]";
        }
        
        StringBuilder builder = new StringBuilder("[");
        
        for (int i = 0; i < length; i++) {
            Object element = Array.get(object, i);
            builder.append(appendString(element));
            
            if (i < length - 1) {
                builder.append(", ");
            }
        }
        
        builder.append(']');
        return builder.toString();
    }

    /**
     * Converts an element to its string representation, handling null values.
     *
     * <p>If the element is null, returns the string "null". Otherwise, delegates to
     * {@link ObjectToStringConverter} to generate the string representation of the element.</p>
     *
     * @param element the array element to convert to string
     * @return the string representation of the element, or "null" if the element is null
     */
    private String appendString(Object element) {
        return element == null ? "null" : objectToStringStrategy.toString(element);
    }
}
