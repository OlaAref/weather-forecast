package com.olaaref.weather.aop.logger.template.interpolation.registrar;

import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * This class is responsible for converting elapsed time in nanoseconds to a human-readable duration format and register it to {@link StringSupplierLookup} instance.
 *
 * <p>This registrar is a component of the message interpolation system for AOP logging.
 * When registered, it provides access to the following variable that can be used in log message templates:</p>
 *
 * <ul>
 *   <li><strong>${elapsed}</strong> - The elapsed time formatted as a {@link Duration} string.
 *       <br>Example: "PT0.123456789S" for 123,456,789 nanoseconds (0.123456789 seconds)</li>
 * </ul>
 *
 * <p>The elapsed time is typically measured in nanoseconds for high precision timing of method executions
 * and is converted to a standard {@link Duration} string representation.</p>
 *
 * <p>Usage example in a log message template:</p>
 * <pre>
 * "Method execution took ${elapsed}"
 * </pre>
 *
 * <p>Which might produce a log message like:</p>
 * <pre>
 * "Method execution took PT0.345S"
 * </pre>
 *
 * @see StringSupplierRegistrar
 * @see StringSupplierLookup
 * @see Duration
 */
public class ElapsedStringSupplierRegistrar implements StringSupplierRegistrar<Long>{
    private static final String ELAPSED_KEY = "elapsed";

    @Override
    public void register(StringSupplierLookup stringSupplierLookup, Long source) {
        stringSupplierLookup.addStringSupplier(ELAPSED_KEY, () -> getElapsedDuration(source));
    }

    private String getElapsedDuration(Long source) {
        return Duration.ofNanos(source).toString();
    }
}
