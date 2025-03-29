package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * {@link ToStringConverter} implementation for Spring Data's {@link Pageable} interface, using {@link ToStringBuilder} to
 * return a String representation of pagination parameters.
 *
 * <p>This strategy handles Spring Data's Pageable interface, which represents pagination information
 * (page number, page size, and sort details). It formats these pagination parameters into a structured string.</p>
 *
 * <p>The implementation uses Apache Commons Lang's {@link ToStringBuilder} to create a structured representation
 * of the Pageable object. For paged results, it includes page number, size, and sort information. For unpaged results,
 * it simply outputs "UNPAGED".</p>
 *
 * <p>Example output:</p>
 * <pre>
 * // For a paged result:
 * // Results in: "[page=0,size=20,sort=id: ASC]"
 *
 * // For an unpaged result:
 * // Results in: "[UNPAGED]"
 * </pre>
 *
 * @see ToStringConverter
 * @see Pageable
 * @see ToStringBuilder
 */
public class PageableToStringConverter implements ToStringConverter {
    /**
     * Determines if the given object type is a pageable instance.
     *
     * <p>This implementation checks if the object is an instance of Spring Data's {@link Pageable} interface.</p>
     *
     * @param object the object to check support for
     * @return true if the object is a Pageable, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof Pageable;
    }

    /**
     * Converts the given Pageable object to its string representation.
     *
     * <p>This method creates a string representation of a Spring Data Pageable object,
     * including pagination parameters when available. The representation includes:</p>
     *
     * <ul>
     *   <li>For paged results: page number, page size, and sort information</li>
     *   <li>For unpaged results: simply the string "UNPAGED"</li>
     * </ul>
     *
     * <p>The implementation uses Apache Commons Lang's {@link ToStringBuilder} with
     * {@link ToStringStyle#NO_CLASS_NAME_STYLE} to create a clean, readable output.</p>
     *
     * @param object the Pageable object to convert to string
     * @return the string representation of the Pageable, including pagination parameters
     */
    @Override
    public String toString(Object object) {
        final Pageable pageable = (Pageable) object;

        final ToStringBuilder toStringBuilder = new ToStringBuilder(pageable, ToStringStyle.NO_CLASS_NAME_STYLE);
        if (pageable.isPaged()) {
            toStringBuilder
                    .append("page", pageable.getPageNumber())
                    .append("size", pageable.getPageSize())
                    .append("sort", pageable.getSort());
        } else {
            toStringBuilder.append("UNPAGED");
        }
        return toStringBuilder.toString();
    }
}
