package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Slice;

/**
 * {@link ToStringConverter} implementation for Spring Data's {@link Slice} interface, using {@link IterableToStringConverter}
 * to return a String representation of the slice content and metadata.
 *
 * <p>This strategy handles Spring Data's Slice interface, which represents a portion or slice of data with pagination information.
 * It formats both the pagination metadata (page number, size, sort, etc.) and the actual content of the slice.</p>
 *
 * <p>The implementation uses Apache Commons Lang's {@link ToStringBuilder} to create a structured representation
 * of the Slice object, including pagination details when available. The slice content is formatted using
 * {@link IterableToStringConverter} to maintain consistent collection formatting throughout the application.</p>
 *
 * <p>Example output:</p>
 * <pre>
 * // For a paged result with content [1, 2, 3]:
 * // Results in: "[page=0,size=10,sort=id: ASC,numberOfElements=3,content=[1, 2, 3]]"
 *
 * // For an unpaged result:
 * // Results in: "[numberOfElements=3,content=[1, 2, 3]]"
 * </pre>
 *
 * @see ToStringConverter
 * @see IterableToStringConverter
 * @see Slice
 * @see ToStringBuilder
 */
public class SliceToStringConverter implements ToStringConverter {

    private IterableToStringConverter iterableToStringStrategy;

    /**
     * Determines if the given object type is a {@link Slice}.
     *
     * <p>This implementation checks if the object is an instance of Spring Data's {@link Slice} interface.</p>
     *
     * @param object the object to check support for
     * @return true if the object is a Slice, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof Slice<?>;
    }

    /**
     * Converts the given Slice object to its string representation.
     *
     * <p>This method creates a detailed string representation of a Spring Data Slice object,
     * including both pagination metadata and the slice content. The representation includes:</p>
     *
     * <ul>
     *   <li>For paged results: page number, page size, and sort information</li>
     *   <li>For all results (paged or unpaged): number of elements in the current slice and the slice content</li>
     * </ul>
     *
     * <p>The slice content is formatted using {@link IterableToStringConverter} to maintain consistent
     * collection formatting throughout the application.</p>
     *
     * <p>Unlike {@link PageToStringConverter}, this implementation does not include totalElements or totalPages
     * information, as Slice objects do not track the total number of elements or pages.</p>
     *
     * @param object the Slice object to convert to string
     * @return the string representation of the Slice, including pagination metadata and content
     */
    @Override
    public String toString(Object object) {
        Slice<?> slice = (Slice<?>) object;
        ToStringBuilder toStringBuilder = new ToStringBuilder(slice, ToStringStyle.NO_CLASS_NAME_STYLE);

        if(slice.getPageable().isPaged()) {
            toStringBuilder
                    .append("page", slice.getPageable().getPageNumber())
                    .append("size", slice.getPageable().getPageSize())
                    .append("sort", slice.getPageable().getSort());
        }

        toStringBuilder
                .append("numberOfElements", slice.getNumberOfElements())
                .append("content", iterableToStringStrategy.toString(slice.getContent()));

        return toStringBuilder.toString();
    }
}
