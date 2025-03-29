package com.olaaref.weather.aop.logger.properties;

import com.olaaref.weather.aop.logger.template.interpolation.converter.ReflectionToStringConverter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the reflection-based toString strategy.
 * <p>
 * This class configures how the {@link ReflectionToStringConverter} converts objects to strings
 * using reflection. It allows customization of which classes should be processed with reflection,
 * and what values should be excluded from the string representation.
 * <p>
 * The properties are configured with the prefix {@code weather.aop.logger.reflection-to-string}.
 *
 * @see ReflectionToStringConverter
 */

@Getter
@Setter
@ConfigurationProperties(prefix = ReflectionToStringProperties.PREFIX)
public class ReflectionToStringProperties {
    public static final String PREFIX = AopLoggersProperties.PREFIX + ".reflection-to-string";

    /**
     * List of base class names that should be processed using reflection-based toString.
     * <p>
     * The {@link ReflectionToStringConverter} will use reflection to convert an object to string
     * if the object's class is assignable from any of these base classes.
     * <p>
     * Default is an empty array, meaning no classes will be processed by default.
     */
    private String[] baseClasses = new String[]{};

    /**
     * Whether to exclude null values from the string representation.
     * <p>
     * When set to true, fields with null values will not appear in the output string.
     * <p>
     * Default is true.
     */
    private boolean excludeNullValues = true;

    /**
     * Whether to exclude empty string values from the string representation.
     * <p>
     * When set to true, fields containing empty strings (length = 0) will not appear in the output string.
     * <p>
     * Default is true.
     */
    private boolean excludeEmptyValues = true;

    /**
     * Whether to exclude zero numeric values from the string representation.
     * <p>
     * When set to true, fields containing numeric values equal to zero will not appear in the output string.
     * <p>
     * Default is true.
     */
    private boolean excludeZeroValues = true;

    /**
     * List of field names to exclude from the string representation.
     * <p>
     * Fields with these names will never appear in the output string, regardless of their values.
     * <p>
     * By default, this is empty, but security-sensitive fields (username, password, passphrase, secret)
     * are automatically added during initialization.
     */
    private String[] excludeFieldNames = new String[]{};


    public void addExcludeFieldNames(String value) {
        excludeFieldNames = ArrayUtils.add(excludeFieldNames, value);
    }

    /**
     * This method adds security-sensitive field names (username, password, passphrase, secret)
     * to the list of excluded field names to prevent sensitive information from appearing in logs.
     */
    @PostConstruct
    void postConstruct() {
        excludeFieldNames = ArrayUtils.insert(0, excludeFieldNames, "username", "password", "passphrase", "secret");

    }
}
