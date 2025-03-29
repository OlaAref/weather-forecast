package com.olaaref.weather.aop.logger.template.interpolation.registrar;

import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;

/**
 * Registers the extracted {@link Exception} details like exception type and exception message to {@link StringSupplierLookup} instance.
 * This class is responsible for extracting and formatting exception details for use in log messages.
 *
 * <p>This registrar is a component of the message interpolation system for AOP logging.
 * When registered, it provides access to the following variable that can be used in log message templates:</p>
 *
 * <ul>
 *   <li><strong>${exception}</strong> - The exception details in the format: "type=ExceptionType, message=exceptionMessage".
 *       <br>Example: "type=IllegalArgumentException, message=Invalid input parameter"</li>
 * </ul>
 *
 * <p>Usage example in a log message template:</p>
 * <pre>
 * "Method execution failed with error: ${exception}"
 * </pre>
 *
 * <p>Which might produce a log message like:</p>
 * <pre>
 * "Method execution failed with error: type=NullPointerException, message=Cannot invoke method on null object"
 * </pre>
 *
 * @see StringSupplierRegistrar
 * @see StringSupplierLookup
 * @see Throwable
 */
public class ExceptionStringSupplierRegistrar implements StringSupplierRegistrar<Throwable> {
    private static final String EXCEPTION_KEY = "exception";

    @Override
    public void register(StringSupplierLookup stringSupplierLookup, Throwable source) {
        stringSupplierLookup.addStringSupplier(EXCEPTION_KEY, () -> getExceptionDetails(source));
    }

    private String getExceptionDetails(Throwable exception) {
        return "type=" + exception.getClass().getSimpleName() + ", message=" + exception.getMessage();
    }
}
