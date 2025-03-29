package com.olaaref.weather.aop.logger.template.interpolation.registrar;

import java.util.function.Supplier;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringLookup;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;
import org.aspectj.lang.JoinPoint;

/**
 * Strategy interface for registering {@link Supplier <String>} instances to a {@link StringLookup}.
 * This interface is a key component of the template interpolation system, allowing different types of
 * information to be dynamically provided to string templates.
 *
 * <p>The StringSupplierRegistrar follows the Strategy pattern, where each implementation knows how to
 * extract and format specific types of information from a source object (represented by the generic
 * type parameter {@code T}). When the {@link #register} method is called, the implementation adds one
 * or more string suppliers to the provided {@link StringLookup} instance.</p>
 *
 * <p>These registered suppliers are later used by {@link StringSubstitutor} to resolve variable
 * placeholders in message templates. The suppliers are only invoked when their values are actually
 * needed, allowing for lazy evaluation of potentially expensive operations.</p>
 *
 * <p><strong>Key Characteristics:</strong></p>
 * <ul>
 *   <li>Type-specific: Each implementation handles a specific type of source object</li>
 *   <li>Reusable: The same registrar can be used with different source instances</li>
 *   <li>Composable: Multiple registrars can populate a single StringSupplierLookup</li>
 * </ul>
 *
 * <p><strong>Implementations:</strong></p>
 * <ul>
 *   <li>{@link JoinPointStringSupplierRegistrar} - Registers suppliers for AspectJ JoinPoint information</li>
 *   <li>{@link ExceptionStringSupplierRegistrar} - Registers suppliers for exception details</li>
 *   <li>{@link ElapsedStringSupplierRegistrar} - Registers suppliers for elapsed time information</li>
 *   <li>{@link ReturnValueStringSupplierRegistrar} - Registers suppliers for return value of a method</li>
 * </ul>
 *
 * @param <T> the type of source object from which string values will be extracted
 * @see StringSupplierLookup
 * @see StringSubstitutor
 * @see StringLookup
 */
public interface StringSupplierRegistrar<T> {
    /**
     * Registers one or more string suppliers to the provided {@link StringLookup} instance.
     * Each supplier is associated with a specific key and will generate string values based on the
     * provided source object when needed.
     *
     * <p>Implementations should:</p>
     * <ul>
     *   <li>Extract relevant information from the source object</li>
     *   <li>Create suppliers that can format this information as strings</li>
     *   <li>Register these suppliers with appropriate keys to the lookup</li>
     * </ul>
     *
     * <p>Note that suppliers are not evaluated immediately - they will only be invoked when
     * their values are requested during string substitution.</p>
     *
     * @param stringSupplierLookup the lookup instance to register suppliers with
     * @param source the source object from which to extract information
     */
    void register(StringSupplierLookup stringSupplierLookup, T source);

}
