/**
 * The interpolation package provides a flexible and extensible string interpolation system for the AOP logger.
 * 
 * <p>This package implements a template-based string interpolation mechanism that allows dynamic values to be
 * inserted into log message templates at runtime. The system is designed to work with AspectJ join points
 * and supports various types of information that can be included in log messages.</p>
 * 
 * <h2>Core Components</h2>
 * 
 * <h3>String Substitution</h3>
 * <ul>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor} - 
 *       Performs the actual string interpolation by replacing variables in the format {variableName} 
 *       with their corresponding values</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.lookup.StringLookup} - 
 *       Interface for looking up string values by key during variable resolution</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup} - 
 *       Default implementation of StringLookup that uses a map of Supplier<String> instances to 
 *       provide dynamic string values</li>
 * </ul>
 * 
 * <h3>Value Registration</h3>
 * <ul>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.registrar.StringSupplierRegistrar} - 
 *       Strategy interface for registering Supplier<String> instances to a StringLookup</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.registrar.JoinPointStringSupplierRegistrar} - 
 *       Registers suppliers for AspectJ JoinPoint information (method signature and parameters)</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.registrar.ReturnValueStringSupplierRegistrar} - 
 *       Registers suppliers for method return values</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.registrar.ExceptionStringSupplierRegistrar} - 
 *       Registers suppliers for exception details</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.registrar.ElapsedStringSupplierRegistrar} - 
 *       Registers suppliers for method execution time information</li>
 * </ul>
 * 
 * <h3>Object to String Conversion</h3>
 * <ul>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter} - 
 *       Strategy interface for converting objects to their string representation</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.selector.ToStringConverterSelector} - 
 *       Factory interface for providing appropriate ToStringConverter implementations</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.selector.DefaultToStringConverterSelector} - 
 *       Default implementation of ToStringConverterSelector</li>
 * </ul>
 * 
 * <h3>Configuration</h3>
 * <ul>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.config.StringSubstitutorConfig} - 
 *       Spring Configuration class for registering StringSubstitutor and various StringSupplierRegistrar implementations</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.config.ToStringConverterConfig} - 
 *       Spring Configuration class for registering core string conversion strategies</li>
 *   <li>{@link com.olaaref.weather.aop.logger.template.interpolation.config.SpringDataDomainToStringConverterConfig} - 
 *       Spring Configuration class for registering strategies for Spring Data domain objects</li>
 * </ul>
 * 
 * <h2>Available Variables</h2>
 * 
 * <p>The following variables are available for use in log message templates:</p>
 * <ul>
 *   <li><strong>{method}</strong> - The method signature</li>
 *   <li><strong>{parameters}</strong> - The method parameter values</li>
 *   <li><strong>{named-parameters}</strong> - The method parameter names and values</li>
 *   <li><strong>{return-value}</strong> - The method return value</li>
 *   <li><strong>{exception}</strong> - Exception details (type and message)</li>
 *   <li><strong>{elapsed}</strong> - The method execution time</li>
 *   <li><strong>{elapsed-time-limit}</strong> - The configured time limit threshold</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * 
 * <pre>
 * // Create a template with variables
 * String template = "Method {method} executed in {elapsed}ms with result: {return-value}";
 * 
 * // Create a lookup and register suppliers
 * StringSupplierLookup lookup = new StringSupplierLookup();
 * joinPointRegistrar.register(lookup, joinPoint);
 * elapsedRegistrar.register(lookup, 150L); // 150ms execution time
 * returnValueRegistrar.register(lookup, result);
 * 
 * // Perform substitution
 * StringSubstitutor substitutor = new StringSubstitutor();
 * String message = substitutor.substitute(template, lookup);
 * 
 * // Result: "Method String getUserById(Long) executed in 150ms with result: User{id=123, name='John'}"
 * </pre>
 * 
 * @see com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor
 * @see com.olaaref.weather.aop.logger.template.interpolation.lookup.StringLookup
 * @see com.olaaref.weather.aop.logger.template.interpolation.registrar.StringSupplierRegistrar
 * @see com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter
 */
package com.olaaref.weather.aop.logger.template.interpolation;