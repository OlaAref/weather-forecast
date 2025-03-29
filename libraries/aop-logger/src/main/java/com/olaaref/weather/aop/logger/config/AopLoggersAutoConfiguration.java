package com.olaaref.weather.aop.logger.config;

import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} for AOP
 * Loggers. This class serves as the entry point for Spring Boot's auto-configuration mechanism
 * to automatically configure the AOP logging framework.
 * <p>
 * The auto-configuration is conditionally enabled based on the {@code weather.aop.logger.enabled}
 * property. By default, it is enabled unless explicitly disabled by setting the property to
 * {@code false}.
 * <p>
 * This class imports {@link AopLoggersConfiguration}, which in turn imports and configures all
 * necessary components for the AOP logging framework, including various advice configurations
 * for different logging scenarios (before, after, around, transaction-related, etc.).
 * <p>
 * The configuration uses {@code proxyBeanMethods = false} for performance optimization, as
 * there are no bean dependencies between the configuration methods.
 *
 * @see AopLoggersConfiguration
 * @see AopLoggersProperties
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = AopLoggersProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Import(AopLoggersConfiguration.class)
public class AopLoggersAutoConfiguration {
}
