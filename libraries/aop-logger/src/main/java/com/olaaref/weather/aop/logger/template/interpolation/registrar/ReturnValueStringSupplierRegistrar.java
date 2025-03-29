package com.olaaref.weather.aop.logger.template.interpolation.registrar;

import com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.dto.ReturnValueInfo;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.selector.ToStringConverterSelector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for extracting and formatting the return value of a method
 * invocation for use in log messages and register it to {@link StringSupplierLookup} instance.
 *
 * <p>This registrar is an important component of the message interpolation system for AOP logging.
 * When registered, it provides access to the following variable that can be used in log message templates:</p>
 *
 * <ul>
 *   <li><strong>${return-value}</strong> - The actual return value of the method call, formatted using the
 *       appropriate {@link ToStringConverter} for the return value type.
 *       <br>For void methods, the string "none" is returned.
 *       <br>For null return values, the string "null" is returned.</li>
 * </ul>
 *
 * <p>This registrar uses the {@link ToStringConverterSelector} to determine the appropriate string
 * representation for the return value, allowing for customized formatting of complex objects.</p>
 *
 * <p>Usage example in a log message template:</p>
 * <pre>
 * "Method execution completed with return value: ${return-value}"
 * </pre>
 *
 * <p>Which might produce log messages like:</p>
 * <pre>
 * "Method execution completed with return value: {id=123, name=\"John\", active=true}"
 * "Method execution completed with return value: none"
 * "Method execution completed with return value: null"
 * </pre>
 *
 * <p>This class implements {@link StringSupplierRegistrar} for consistency with other registrars
 * in the system. The source parameter for this registrar is a {@link ReturnValueInfo} object that
 * contains both the {@link JoinPoint} and the return value.</p>
 *
 * @author Andy Lian
 * @see StringSupplierLookup
 * @see ToStringConverterSelector
 * @see JoinPointStringSupplierRegistrar
 */
public class ReturnValueStringSupplierRegistrar implements StringSupplierRegistrar<ReturnValueInfo> {

    private static final String RETURN_VALUE_KEY = "return-value";

    private static final String NO_RETURN_VALUE_STRING = "none";

    private static final String NULL_RETURN_VALUE_STRING = "null";

    @Autowired
    private ToStringConverterSelector toStringConverterSelector;

    /**
     * Registers a string supplier for the return value to the provided {@link StringSupplierLookup}.
     * This method creates a supplier that will generate a string representation of the method's
     * return value when needed for message interpolation.
     *
     * @param stringSupplierLookup the lookup instance to register the supplier with
     * @param source the ReturnValueInfo containing both the join point and return value
     */
    @Override
    public void register(StringSupplierLookup stringSupplierLookup, ReturnValueInfo source) {
        stringSupplierLookup.addStringSupplier(RETURN_VALUE_KEY, () -> getReturnValueString(source));
    }

    /**
     * Determines the string representation of a method's return value.
     * For void methods, returns a predefined string indicating no return value.
     * For non-void methods, converts the return value to a string using the appropriate
     * {@link ToStringConverter}.
     * For null return values, returns a predefined string indicating a null value.
     *
     * @return a string representation of the return value, "none" for void methods, or "null" for null values
     */
    private String getReturnValueString(ReturnValueInfo source) {
        MethodSignature methodSignature = getMethodSignature(source.joinPoint());
        Object returnValue = source.returnValue();

        // Check if the method return type is void
        if(void.class.equals(methodSignature.getReturnType())) return NO_RETURN_VALUE_STRING;
        // Check if the return value is null
        if(returnValue == null) return NULL_RETURN_VALUE_STRING;

        return toStringConverterSelector.findConverterOrDefault(returnValue).toString(returnValue);
    }

    private MethodSignature getMethodSignature(JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }
}
