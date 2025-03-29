package com.olaaref.weather.aop.logger.template.interpolation.selector;

import com.olaaref.weather.aop.logger.template.interpolation.converter.ObjectToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.StringSupplierRegistrar;

/**
 * Factory interface responsible for providing appropriate {@link ToStringConverter} implementations
 * for converting objects to their string representation. This factory is a core component of the
 * message interpolation system, enabling consistent and extensible object-to-string conversion.
 *
 * <p>The factory follows the Strategy pattern, where it selects the most appropriate strategy
 * for a given object type from all available {@link ToStringConverter} implementations. If no
 * specific strategy supports the object, it falls back to the default {@link ObjectToStringConverter}.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *   <li>Determine the most appropriate converter for a given object type</li>
 *   <li>Provide a fallback converter when no specific converter is applicable</li>
 *   <li>Enable extensibility by allowing new converters to be registered</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * // Inject the factory
 * @Autowired
 * private ToStringConverterSelector toStringConverterSelector;
 *
 * // Use it to convert objects to strings
 * public String formatValue(Object value) {
 *     ToStringStrategy strategy = toStringConverterSelector.findOrDefault(value);
 *     return strategy.toString(value);
 * }
 * </pre>
 *
 * <p>The default implementation {@link DefaultToStringConverterSelector} uses Spring's dependency
 * injection to collect all available {@link ToStringConverter} beans and selects the first one
 * that supports the given object type.</p>
 *
 * @see ToStringConverter
 * @see DefaultToStringConverterSelector
 * @see ObjectToStringConverter
 * @see StringSupplierRegistrar
 */
public interface ToStringConverterSelector {

    /**
     * Finds the most appropriate {@link ToStringConverter} for the given object or returns the default
     * Converter if no specific Converter supports the object.
     *
     * <p>This method evaluates all available converters by calling their {@link ToStringConverter#supports(Object)}
     * method and selects the first one that returns {@code true}. If no converter supports the object
     * or if the object is {@code null}, it returns the default {@link ObjectToStringConverter}.</p>
     *
     * <p>The selection process typically follows this order:</p>
     * <ol>
     *   <li>Check if the object is null (return default converter)</li>
     *   <li>Iterate through all available converters</li>
     *   <li>Return the first converter that supports the object</li>
     *   <li>If no converter supports the object, return the default converter</li>
     * </ol>
     *
     * @param object the object to find a converter for, may be {@code null}
     * @return a {@link ToStringConverter} that can convert the object to string, never {@code null}
     */
    ToStringConverter findConverterOrDefault(Object object);

}
