package com.olaaref.weather.aop.logger.template.interpolation.lookup;

import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;

/**
 * Interface for looking up string values by key. This interface is primarily used by
 * {@link StringSubstitutor} to resolve variable values during string interpolation.
 *
 * <p>The default implementation {@link StringSupplierLookup} uses a map of {@link java.util.function.Supplier}
 * instances to provide dynamic string values.
 *
 */
public interface StringLookup {

    /**
     * Looks up a string value for the given key.
     *
     * @param key the key to look up
     * @return the string value associated with the key, or null if not found
     */
    String lookup(String key);
}
