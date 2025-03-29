package com.olaaref.weather.aop.logger.template.interpolation.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * {@link ToStringConverter} implementation for Spring Data's {@link Page} interface, using {@link IterableToStringConverter}
 * to return a String representation of the page content and metadata.
 *
 * <p>This strategy handles Spring Data's Page interface, which represents a page of data with pagination information.
 * It formats both the pagination metadata (page number, size, sort, etc.) and the actual content of the page.</p>
 *
 * <p>The implementation uses Apache Commons Lang's {@link ToStringBuilder} to create a structured representation
 * of the Page object, including pagination details when available. The page content is formatted using
 * {@link IterableToStringConverter} to maintain consistent collection formatting throughout the application.</p>
 *
 * <p>Example output:</p>
 * <pre>
 * // For a paged result with content [1, 2, 3]:
 * // Results in: "[page=0,size=10,sort=id: ASC,totalElements=3,totalPages=1,numberOfElements=3,content=[1, 2, 3]]"
 *
 * // For an unpaged result:
 * // Results in: "[numberOfElements=3,content=[1, 2, 3]]"
 * </pre>
 *
 * @see ToStringConverter
 * @see IterableToStringConverter
 * @see Page
 * @see ToStringBuilder
 */
public class PageToStringConverter implements ToStringConverter {
    @Autowired
    private IterableToStringConverter iterableToStringStrategy;

    /**
     * Determines if the given object type is a {@link Page}.
     *
     * <p>This implementation checks if the object is an instance of Spring Data's {@link Page} interface.</p>
     *
     * @param object the object to check support for
     * @return true if the object is a Page, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof Page<?>;
    }

    /**
     * Converts the given Page object to its string representation.
     *
     * <p>This method creates a detailed string representation of a Spring Data Page object,
     * including both pagination metadata and the page content. The representation includes:</p>
     *
     * <ul>
     *   <li>For paged results: page number, page size, sort information, total elements, and total pages</li>
     *   <li>For all results (paged or unpaged): number of elements in the current page and the page content</li>
     * </ul>
     *
     * <p>The page content is formatted using {@link IterableToStringConverter} to maintain consistent
     * collection formatting throughout the application.</p>
     *
     * @param object the Page object to convert to string
     * @return the string representation of the Page, including pagination metadata and content
     */
    @Override
    public String toString(Object object) {
        Page<?> page = (Page<?>) object;
        ToStringBuilder toStringBuilder = new ToStringBuilder(page, ToStringStyle.NO_CLASS_NAME_STYLE);

        if(page.getPageable().isPaged()){
            toStringBuilder
                    .append("page", page.getNumber())
                    .append("size", page.getSize())
                    .append("sort", page.getSort())
                    .append("totalElements", page.getTotalElements())
                    .append("totalPages", page.getTotalPages());
        }

        toStringBuilder
                .append("numberOfElements", page.getNumberOfElements())
                .append("content", iterableToStringStrategy.toString(page.getContent()));

        return toStringBuilder.toString();
    }
}
