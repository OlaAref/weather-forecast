package com.olaaref.weather.aop.logger.template.interpolation.config;

import com.olaaref.weather.aop.logger.properties.ReflectionToStringProperties;
import com.olaaref.weather.aop.logger.template.interpolation.converter.*;
import com.olaaref.weather.aop.logger.template.interpolation.selector.DefaultToStringConverterSelector;
import com.olaaref.weather.aop.logger.template.interpolation.selector.ToStringConverterSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring {@link Configuration} class responsible for registering various {@link ToStringConverter}
 * implementations as Spring beans. This configuration is a core component of the template interpolation
 * system, providing the necessary strategies for converting different object types to their string
 * representations.
 *
 * <p>This configuration class is imported by {@link StringSubstitutorConfig} and works alongside
 * {@link SpringDataDomainToStringConverterConfig} to provide a comprehensive set of string conversion
 * strategies for different object types.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Registers core {@link ToStringConverter} implementations as Spring beans</li>
 *   <li>Configures the {@link ToStringConverterSelector} that selects appropriate strategies</li>
 *   <li>Enables configuration properties for reflection-based string conversion</li>
 * </ul>
 *
 * <p>The strategies registered by this configuration handle various object types including:</p>
 * <ul>
 *   <li>Basic objects (via {@link ObjectToStringConverter})</li>
 *   <li>Complex objects using reflection (via {@link ReflectionToStringConverter})</li>
 *   <li>Arrays (via {@link ArrayToStringConverter})</li>
 *   <li>Collections (via {@link IterableToStringConverter})</li>
 *   <li>Optional values (via {@link OptionalToStringConverter})</li>
 * </ul>
 *
 * @see ToStringConverter
 * @see ToStringConverterSelector
 * @see DefaultToStringConverterSelector
 * @see ReflectionToStringProperties
 * @see StringSubstitutorConfig
 * @see SpringDataDomainToStringConverterConfig
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ReflectionToStringProperties.class)
public class ToStringConverterConfig {

    @Bean
    public ReflectionToStringConverter reflectionToStringConverter() {
        return new ReflectionToStringConverter();
    }

    @Bean
    public ObjectToStringConverter objectToStringConverter() {
        return new ObjectToStringConverter();
    }

    @Bean
    public OptionalToStringConverter optionalToStringConverter() {
        return new OptionalToStringConverter();
    }

    @Bean
    public ArrayToStringConverter arrayToStringConverter() {
        return new ArrayToStringConverter();
    }

    @Bean
    public IterableToStringConverter iterableToStringConverter() {
        return new IterableToStringConverter();
    }

    /**
     * Creates a {@link ToStringConverterSelector} bean that manages and selects appropriate
     * {@link ToStringConverter} implementations for converting objects to their string representation.
     *
     * <p>This factory is only created if no other {@link ToStringConverterSelector} bean is present in the
     * application context. It automatically collects all available {@link ToStringConverter} beans and
     * selects the most appropriate one for a given object type based on the strategy's
     * {@link ToStringConverter#supports(Object)} method.</p>
     *
     * <p>If no specific converter supports an object, it falls back to the {@link ObjectToStringConverter}.</p>
     *
     * @return a new {@link DefaultToStringConverterSelector} instance
     * @see DefaultToStringConverterSelector
     * @see ToStringConverter
     */
    @Bean
    @ConditionalOnMissingBean(ToStringConverterSelector.class)
    public ToStringConverterSelector toStringConverterSelector() {
        return new DefaultToStringConverterSelector();
    }
}
