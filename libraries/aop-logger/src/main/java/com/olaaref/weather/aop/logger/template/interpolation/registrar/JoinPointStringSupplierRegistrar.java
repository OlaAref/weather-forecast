package com.olaaref.weather.aop.logger.template.interpolation.registrar;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.olaaref.weather.aop.logger.annotation.DoNotLog;
import com.olaaref.weather.aop.logger.properties.ReflectionToStringProperties;
import com.olaaref.weather.aop.logger.template.interpolation.converter.ToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringSupplierLookup;
import com.olaaref.weather.aop.logger.template.interpolation.selector.ToStringConverterSelector;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Registers the extracted {@link JoinPoint} details like method signature and method parameters to {@link StringSupplierLookup} instance.
 * This class is responsible for extracting and formatting method and parameter information
 * from AspectJ join points for use in log messages.
 *
 * <p>This registrar is a key component of the message interpolation system for AOP logging. When
 * registered, it provides access to two variables that can be used in log message templates:</p>
 *
 * <ul>
 *   <li><strong>${method}</strong> - The method signature in the format: "ReturnType methodName(ParameterType1, ParameterType2, ...)"
 *       <br>Example: "String getUserById(Long, Boolean)"</li>
 *   <li><strong>${parameters}</strong> - The actual parameter values passed to the method, formatted using the
 *       appropriate {@link ToStringConverter} for each parameter type
 *       <br>Example: "42, true, {id=123, name=\"John\"}"</li>
 * </ul>
 *
 * <p>This registrar uses the {@link ToStringConverterSelector} to determine the appropriate string
 * representation for each parameter value, allowing for customized formatting of complex objects.</p>
 *
 * @see StringSupplierRegistrar
 * @see StringSupplierLookup
 * @see ToStringConverterSelector
 */
public class JoinPointStringSupplierRegistrar implements StringSupplierRegistrar<JoinPoint> {

    private static final String METHOD_KEY = "method";

    private static final String PARAMETERS_KEY = "parameters";

    private static final String NO_PARAMETERS_STRING = "none";

    private static final String REDACTED_PARAMETERS = "*********";

    @Autowired
    private ReflectionToStringProperties reflectionToStringProperties;

    @Autowired
    private ToStringConverterSelector toStringConverterSelector;

    @Override
    public void register(StringSupplierLookup stringSupplierLookup, JoinPoint source) {
        Objects.requireNonNull(stringSupplierLookup, "StringSupplierLookup must not be null");
        Objects.requireNonNull(source, "JoinPoint must not be null");

        stringSupplierLookup.addStringSupplier(METHOD_KEY, () -> methodStringRepresentation(getMethodSignature(source).getMethod()));
        stringSupplierLookup.addStringSupplier(PARAMETERS_KEY, () -> methodParametersStringRepresentation(source));
    }

    /**
     * Extracts the method signature from an AspectJ join point.
     * This method casts the generic signature from the join point to a {@link MethodSignature}.
     *
     * @param joinPoint the AspectJ join point to extract the signature from
     * @return the method signature of the join point
     */
    private MethodSignature getMethodSignature(final JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    /**
     * Formats a method signature as a string in the format: "ReturnType methodName(ParameterType1, ParameterType2, ...)"
     *
     * @param method the method to format
     * @return a string representation of the method signature
     */
    private String methodStringRepresentation(final Method method) {
        return new StringBuilder()
                .append(method.getReturnType().getSimpleName())
                .append(" ")
                .append(method.getName())
                .append("(")
                .append(methodParameterTypes(method.getParameterTypes()))
                .append(")")
                .toString();
    }

    /**
     * Formats the parameter values of a method call as a comma-separated string.
     * If the method has no parameters, returns the string "none".
     *
     * @param joinPoint the AspectJ join point representing the method execution
     * @return a string representation of the method parameters
     */
    private String methodParametersStringRepresentation(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = getMethodSignature(joinPoint);
        final Method method = methodSignature.getMethod();
        final int parameterCount = method.getParameterCount();

        if (parameterCount == 0) {
            return NO_PARAMETERS_STRING;
        }

        final Object[] parameterValues = joinPoint.getArgs();
        if (parameterValues == null || parameterValues.length == 0) {
            return NO_PARAMETERS_STRING;
        }

        final StringBuilder builder = new StringBuilder();

        for (int index = 0; index < parameterCount; index++) {
            appendParameterValue(method, index, builder, parameterValues);
            if (index < parameterCount - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    private void appendParameterValue(Method method, int index, StringBuilder builder, Object[] parameterValues) {
        Parameter parameter = method.getParameters()[index];
        if(isParameterPartiallyIgnored(parameter)){
            ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String[] excludedFields = ArrayUtils.addAll(getExcludedField(parameter), reflectionToStringProperties.getExcludeFieldNames());

            // Create a dynamic filter that excludes the specified fields
            SimpleBeanPropertyFilter propertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(excludedFields);
            SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("doNotLogFilter", propertyFilter);

            // Create a MixIn annotation interface to apply the filter to any object
            objectMapper.addMixIn(Object.class, PropertyFilterMixIn.class);

            try {
                builder.append(objectMapper.writer(filterProvider).writeValueAsString(parameterValues[index]));
            } catch (Exception e) {
                builder.append(REDACTED_PARAMETERS);
            }
        }
        else if(isParameterIgnored(parameter)) {
            builder.append(REDACTED_PARAMETERS);
        }
        else {
            builder.append(toString(parameterValues[index]));
        }
    }

    private Boolean isParameterPartiallyIgnored(Parameter param) {
        return param.isAnnotationPresent(DoNotLog.class) && param.getAnnotation(DoNotLog.class).parameters().length > 0;
    }

    private String[] getExcludedField(Parameter parameter) {
        Set<String> excludedFields = new HashSet<>();
        String[] blackListFields = parameter.getAnnotation(DoNotLog.class).parameters();
        for(Field field : parameter.getType().getDeclaredFields()) {
            for(String excludedField : blackListFields) {
                if(field.getName().equalsIgnoreCase(excludedField)) {
                    excludedFields.add(field.getName());
                    break;
                }
            }
        }
        return excludedFields.toArray(new String[0]);
    }

    /**
     * Converts an object to its string representation using the appropriate {@link ToStringConverter}.
     * The strategy is determined by the {@link ToStringConverterSelector} based on the object's type.
     *
     * @param object the object to convert to a string
     * @return the string representation of the object
     */
    private String toString(Object object) {
        return toStringConverterSelector.findConverterOrDefault(object).toString(object);
    }

    /**
     * Formats an array of parameter types as a comma-separated string of simple class names.
     * If the array is empty, returns an empty string.
     *
     * @param parameterTypes the array of parameter types to format
     * @return a comma-separated string of parameter type names
     */
    private String methodParameterTypes(final Class<?>[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();

        for (int index = 0; index < parameterTypes.length; index++) {
            if (parameterTypes[index] != null) {
                builder.append(parameterTypes[index].getSimpleName());
            } else {
                builder.append("null");
            }

            if (index < parameterTypes.length - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    private boolean isParameterIgnored(Parameter parameter) {
        return parameter.isAnnotationPresent(DoNotLog.class);
    }

    /**
     * MixIn annotation interface used to apply the JsonFilter to any object being serialized.
     * This allows us to dynamically filter properties without requiring the target classes
     * to be annotated with @JsonFilter.
     */
    @JsonFilter("doNotLogFilter")
    private interface PropertyFilterMixIn {
        // This is just a marker interface
    }
}
