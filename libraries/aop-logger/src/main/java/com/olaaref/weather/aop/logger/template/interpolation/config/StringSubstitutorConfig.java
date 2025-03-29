package com.olaaref.weather.aop.logger.template.interpolation.config;

import com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.registrar.*;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring {@link Configuration} class responsible for registering {@link StringSubstitutor} and various
 * {@link StringSupplierRegistrar} implementations as Spring beans. This configuration is a core component
 * of the message interpolation system for AOP logging.
 *
 * <p>The message interpolation system provides string variable substitution capabilities for log messages,
 * allowing dynamic values to be inserted into log templates at runtime. This is achieved through the
 * following components:</p>
 *
 * <ul>
 *   <li>{@link StringSubstitutor} - Performs the actual string interpolation by replacing variables
 *       in the format ${variableName} with their corresponding values</li>
 *   <li>{@link StringSupplierRegistrar} implementations - Register suppliers for different types of
 *       information that can be included in log messages</li>
 *   <li>{@link StringSupplierLookup} - Maintains a registry of variable names and their suppliers</li>
 *   <li>{@link ToStringConverter} implementations - Convert different object types to appropriate
 *       string representations</li>
 * </ul>
 *
 * <p>This configuration imports two additional configuration classes:</p>
 * <ul>
 *   <li>{@link SpringDataDomainToStringConverterConfig} - Registers strategies for Spring Data domain objects</li>
 *   <li>{@link ToStringConverterConfig} - Registers core string conversion strategies</li>
 * </ul>
 *
 * <p>The registrars configured by this class provide access to the following variables in log templates:</p>
 * <ul>
 *   <li><strong>${method}</strong> - The method signature</li>
 *   <li><strong>${parameters}</strong> - The method parameter values</li>
 *   <li><strong>${return-value}</strong> - The method return value</li>
 *   <li><strong>${exception}</strong> - Exception details (type and message)</li>
 *   <li><strong>${elapsed}</strong> - The method execution time</li>
 *   <li><strong>${elapsed-time-limit}</strong> - The configured time limit threshold</li>
 * </ul>
 *
 * @see StringSubstitutor
 * @see StringSupplierRegistrar
 * @see StringSupplierLookup
 * @see ToStringConverterConfig
 * @see SpringDataDomainToStringConverterConfig
 */
@Configuration(proxyBeanMethods = false)
@Import({SpringDataDomainToStringConverterConfig.class, ToStringConverterConfig.class})
public class StringSubstitutorConfig {

    @Bean
    public StringSubstitutor stringSubstitutor(){
        return new StringSubstitutor();
    }

    @Bean
    public JoinPointStringSupplierRegistrar joinPointStringSupplierRegistrar(){
        return new JoinPointStringSupplierRegistrar();
    }

    @Bean
    public ReturnValueStringSupplierRegistrar returnValueStringSupplierRegistrar(){
        return new ReturnValueStringSupplierRegistrar();
    }

    @Bean
    public ExceptionStringSupplierRegistrar exceptionStringSupplierRegistrar(){
        return new ExceptionStringSupplierRegistrar();
    }

    @Bean
    public ElapsedStringSupplierRegistrar elapsedStringSupplierRegistrar(){
        return new ElapsedStringSupplierRegistrar();
    }
}
