package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link ToStringConverter} implementation for {@link Iterable} types, using
 * {@link ObjectToStringConverter} to return a String representation of each element. This strategy converts
 * iterables to a comma-separated list of elements enclosed within square brackets "[]".
 *
 * <p>This strategy handles all Iterable implementations (List, Set, Queue, etc.) and properly manages null elements
 * by representing them as the string "null". For non-null elements, it delegates to {@link ObjectToStringConverter}
 * to generate appropriate string representations.</p>
 *
 * @see ToStringConverter
 * @see ObjectToStringConverter
 */
public class IterableToStringConverter implements ToStringConverter {
    @Autowired
    private ObjectToStringConverter objectToStringStrategy;

    /**
     * Determines if the given object type is an Iterable.
     *
     * <p>This implementation checks if the object is an instance of {@link Iterable}.
     * It supports all Iterable implementations including List, Set, Queue, and custom Iterable types.</p>
     *
     * @param object the object to check support for
     * @return true if the object is an Iterable, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof Iterable<?>;
    }

    /**
     * Converts the given Iterable object to its string representation.
     *
     * <p>This method casts the input object to an Iterable<?>.</p>
     * <p>This method iterates through the Iterable elements using an Iterator, appending each element's string
     * representation to a StringBuilder. Elements are separated by commas and the entire list
     *  is enclosed in square brackets. For empty Iterables, it returns "[]".</p>
     *
     * <p>The implementation handles the first element separately to avoid adding a comma after the last element.</p>
     *
     * @param object the Iterable object to convert to string
     * @return the string representation of the Iterable
     */
    @Override
    public String toString(Object object) {
        Iterable<?> iterable = (Iterable<?>) object;
        if(iterable.iterator().hasNext()) return "[]";

        StringBuilder builder = new StringBuilder("[");
        builder.append(appendString(iterable.iterator().next()));

        while (iterable.iterator().hasNext()){
            builder.append(", ");
            builder.append(appendString(iterable.iterator().next()));
        }

        builder.append("]");
        return builder.toString();
    }

    /**
     * Converts an element to its string representation, handling null values.
     *
     * <p>If the element is null, returns the string "null". Otherwise, delegates to
     * {@link ObjectToStringConverter} to generate the string representation of the element.</p>
     *
     * @param element the Iterable element to convert to string
     * @return the string representation of the element, or "null" if the element is null
     */
    private String appendString(Object element) {
        return element == null ? "null" : objectToStringStrategy.toString(element);
    }
}
