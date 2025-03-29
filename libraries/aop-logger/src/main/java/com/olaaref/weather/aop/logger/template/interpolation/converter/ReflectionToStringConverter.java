package com.olaaref.weather.aop.logger.template.interpolation.converter;

import com.olaaref.weather.aop.logger.properties.ReflectionToStringProperties;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ToStringConverter} that uses reflection to create detailed string
 * representations of objects.
 *
 * <p>This strategy leverages Apache Commons Lang's {@link ReflectionToStringBuilder} to generate
 * string representations by examining an object's fields through reflection. The strategy can be
 * configured to include or exclude specific values based on properties defined in
 * {@link ReflectionToStringProperties}.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Configurable base classes that determine which objects this strategy supports</li>
 *   <li>Ability to exclude null, empty string, and zero numeric values</li>
 *   <li>Support for excluding specific field names (including security-sensitive fields)</li>
 *   <li>Automatic rejection of proxy classes to avoid reflection issues</li>
 * </ul>
 *
 * <p>This strategy is particularly useful for complex domain objects where the default
 * {@code toString()} implementation might not provide sufficient detail for logging or debugging
 * purposes.</p>
 *
 * @see ReflectionToStringProperties
 * @see ToStringConverter
 * @see ReflectionToStringBuilder
 */
public class ReflectionToStringConverter implements ToStringConverter {

    /**
     * Configuration properties for this strategy, injected by Spring.
     * These properties determine which classes are supported and what values should be excluded
     * from the string representation.
     *
     * @see ReflectionToStringProperties
     */
    @Autowired
    private ReflectionToStringProperties reflectionToStringProperties;

    /**
     * List of base classes that this strategy supports, populated during initialization.
     * An object will be supported by this strategy if its class is assignable from any of these base classes.
     */
    public final List<Class<?>> supportedBaseClasses = new ArrayList<>();

    /**
     * Initializes the strategy by loading the configured base classes.
     *
     * <p>This method is automatically called by Spring after dependency injection is complete.
     * It loads each class specified in {@link ReflectionToStringProperties#getBaseClasses()}
     * and adds it to the list of supported base classes.</p>
     *
     * @throws InvalidConfigurationPropertyValueException if any of the configured base class names
     *         cannot be found or loaded
     */
    @PostConstruct
    void postConstruct() {
        String[] baseClassesFromProperties = reflectionToStringProperties.getBaseClasses();
        for (String baseClass : baseClassesFromProperties) {
            try {
                supportedBaseClasses.add(Class.forName(baseClass));
            } catch (ClassNotFoundException e) {
                throw new InvalidConfigurationPropertyValueException(
                        ReflectionToStringProperties.PREFIX + ".base-classes",
                        baseClass,
                        e.getMessage()
                );
            }
        }
    }

    /**
     * Determines if this strategy supports converting the given object to a string.
     *
     * <p>An object is supported if:</p>
     * <ol>
     *   <li>It is not a proxy class (to avoid reflection issues)</li>
     *   <li>Its class is assignable from any of the configured base classes</li>
     * </ol>
     *
     * <p>Proxy classes are explicitly rejected because reflection on proxy objects can lead to
     * unexpected behavior or errors.</p>
     *
     * @param object the object to check support for, must not be null
     * @return true if this strategy can convert the object to string, false otherwise
     */
    @Override
    public boolean supports(Object object) {
        if(Proxy.isProxyClass(object.getClass())) {
            return false;
        }

        for(Class<?> supportedClass: supportedBaseClasses){
            if(supportedClass.isAssignableFrom(object.getClass())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts the given object to its string representation using reflection.
     *
     * <p>This method creates a custom {@link ReflectionToStringBuilder} that:</p>
     * <ul>
     *   <li>Uses {@link ToStringStyle#NO_CLASS_NAME_STYLE} to produce cleaner output which omits class names from the output</li>
     *   <li>Excludes null values if configured to do so</li>
     *   <li>Excludes specific field names as configured</li>
     *   <li>Excludes empty strings and zero numeric values if configured to do so</li>
     * </ul>
     *
     * <p>The resulting string format is typically: {@code [fieldName1=value1, fieldName2=value2]}</p>
     *
     * @param object the object to convert to string
     * @return the string representation of the object
     */
    @Override
    public String toString(Object object) {
        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(object, ToStringStyle.NO_CLASS_NAME_STYLE){
            @Override
            public ToStringBuilder append(String fieldName, Object obj, boolean fullDetail) {
                if(!isExcludedValue(object)) {
                    super.append(fieldName, obj, fullDetail);
                }
                return this;
            }
        };

        builder.setExcludeNullValues(reflectionToStringProperties.isExcludeNullValues());
        builder.setExcludeFieldNames(reflectionToStringProperties.getExcludeFieldNames());

        return builder.toString();
    }

    /**
     * Determines if a zero numeric value should be excluded from the string representation.
     *
     * <p>A value is considered an excluded zero value if:</p>
     * <ul>
     *   <li>It is an instance of {@link Number}</li>
     *   <li>The {@code excludeZeroValues} property is true</li>
     *   <li>The numeric value equals zero</li>
     * </ul>
     *
     * @param object the value to check for zero value exclusion
     * @return true if the value is a zero numeric value that should be excluded, false otherwise
     */
    private boolean isExcludedZeroValues(Object object){
        return object instanceof Number numberValue
                && reflectionToStringProperties.isExcludeZeroValues()
                && Double.compare(numberValue.doubleValue(), 0.0) == 0;
    }

    /**
     * Determines if an empty string value should be excluded from the string representation.
     *
     * <p>A value is considered an excluded empty value if:</p>
     * <ul>
     *   <li>It is an instance of {@link CharSequence}</li>
     *   <li>The {@code excludeEmptyValues} property is true</li>
     *   <li>The string length is zero</li>
     * </ul>
     *
     * @param object the value to check for empty string exclusion
     * @return true if the value is an empty string that should be excluded, false otherwise
     */
    private boolean isExcludedEmptyValues(Object object){
        return object instanceof CharSequence charSequence
                && reflectionToStringProperties.isExcludeEmptyValues()
                && charSequence.isEmpty();
    }

    /**
     * Determines if a value should be excluded from the string representation.
     *
     * <p>A value is excluded if it is either:</p>
     * <ul>
     *   <li>An empty string (when {@code excludeEmptyValues} is true)</li>
     *   <li>A zero numeric value (when {@code excludeZeroValues} is true)</li>
     * </ul>
     *
     * @param object the value to check for exclusion
     * @return true if the value should be excluded, false otherwise
     */
    private boolean isExcludedValue(Object object){
        return isExcludedEmptyValues(object) || isExcludedZeroValues(object);
    }
}
