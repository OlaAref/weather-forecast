package com.olaaref.weather.aop.logger.template.interpolation.selector;

import com.olaaref.weather.aop.logger.template.interpolation.converter.ObjectToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link ToStringConverterSelector} interface that manages and selects
 * appropriate {@link ToStringConverter} implementations for converting objects to their string representation.
 *
 * <p>This factory uses Spring's dependency injection to collect all available {@link ToStringConverter}
 * beans and selects the most appropriate one for a given object type based on the Converter's
 * {@link ToStringConverter#supports(Object)} method.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Automatically collects all {@link ToStringConverter} beans during initialization</li>
 *   <li>Filters out the {@link ObjectToStringConverter} from the collection as it serves as the default fallback</li>
 *   <li>Uses parallel stream processing for efficient Converter selection</li>
 *   <li>Provides a consistent fallback mechanism using {@link ObjectToStringConverter}</li>
 * </ul>
 *
 * <p>This implementation follows the Converter pattern, allowing for extensible object-to-string conversion
 * by supporting custom {@link ToStringConverter} implementations that can be automatically discovered
 * and utilized by the factory.</p>
 *
 * @author Andy Lian
 * @see ToStringConverterSelector
 * @see ToStringConverter
 * @see ObjectToStringConverter
 */
public class DefaultToStringConverterSelector implements ToStringConverterSelector{
    /**
     * The default fallback strategy used when no specific strategy supports an object
     * or when the object is null. This strategy is autowired by Spring and is guaranteed
     * to support all object types as its {@link ObjectToStringConverter#supports(Object)}
     * method always returns true.
     */
    @Autowired
    private ObjectToStringConverter objectToStringConverter;

    /**
     * Provider for all {@link ToStringConverter} beans registered in the Spring application context.
     * This is used to collect all available strategies during initialization.
     */
    @Autowired
    private ObjectProvider<ToStringConverter> toStringStrategiesProvider;

    /**
     * The collection of all available {@link ToStringConverter} implementations, excluding
     * the {@link ObjectToStringConverter} which is kept separate as the default fallback.
     * This list is populated during the {@link #postConstruct()} method.
     */
    private List<ToStringConverter> toStringStrategies = new ArrayList<>();

    /**
     * Initializes the factory by collecting all {@link ToStringConverter} beans from the Spring
     * application context, excluding the {@link ObjectToStringConverter}.
     *
     * <p>This method is automatically called by Spring after dependency injection is complete.
     * It filters out the {@link ObjectToStringConverter} from the collection as it serves as the
     * default fallback strategy and should not be part of the regular selection process.</p>
     */
    @PostConstruct
    void postConstruct() {
        toStringStrategies =
                toStringStrategiesProvider.stream()
                        .filter(toStringConverter -> !(toStringConverter instanceof ObjectToStringConverter))
                        .toList();
    }

    /**
     * Finds the most appropriate {@link ToStringConverter} for the given object or returns the default
     * {@link ObjectToStringConverter} if no specific strategy supports the object.
     *
     * <p>The selection process follows this order:</p>
     * <ol>
     *   <li>If the object is null, return the default {@link ObjectToStringConverter}</li>
     *   <li>If no strategies are available (toStringStrategies is null or empty), return the default strategy</li>
     *   <li>Use parallel stream processing to find the first strategy that supports the object</li>
     *   <li>If no strategy supports the object, return the default {@link ObjectToStringConverter}</li>
     * </ol>
     *
     * <p>This implementation uses parallel stream processing for efficient strategy selection,
     * which can improve performance when many strategies are registered or when the application
     * processes a high volume of objects.</p>
     *
     * @param object the object to find a strategy for, may be {@code null}
     * @return a {@link ToStringConverter} that can convert the object to string, never {@code null}
     */
    @Override
    public ToStringConverter findConverterOrDefault(Object object) {
        if (object == null || toStringStrategies == null || toStringStrategies.isEmpty()) {
            return objectToStringConverter;
        }

        return toStringStrategies.parallelStream()
                .filter(toStringConverter -> toStringConverter.supports(object))
                .findFirst()
                .orElse(objectToStringConverter);
    }
}
