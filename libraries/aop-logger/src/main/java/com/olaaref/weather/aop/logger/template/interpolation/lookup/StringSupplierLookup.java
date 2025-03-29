package com.olaaref.weather.aop.logger.template.interpolation.lookup;

import com.olaaref.weather.aop.logger.template.interpolation.registrar.StringSupplierRegistrar;
import com.olaaref.weather.aop.logger.template.interpolation.substitutor.StringSubstitutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Default implementation of {@link StringLookup} used by {@link StringSubstitutor} to resolve string values by key.
 * This class maintains a map of string suppliers that can dynamically generate values when needed.
 *
 * <p>The StringSupplierLookup serves as a central registry for variable values in the string interpolation system.
 * It stores {@link Supplier <String>} instances rather than static string values, allowing for dynamic value
 * resolution at the time of lookup rather than at registration time.</p>
 *
 * <p>This class is primarily used in conjunction with various {@link StringSupplierRegistrar} implementations
 * that populate it with suppliers for different types of information (method parameters, return values, exceptions, etc.).</p>
 *
 * <p><strong>Thread Safety:</strong> This implementation is thread-safe. It uses ConcurrentHashMap internally
 * to ensure that concurrent modifications to the map are handled safely without requiring external
 * synchronization.</p>
 *
 * @see StringLookup
 * @see StringSubstitutor
 * @see StringSupplierRegistrar
 */
public class StringSupplierLookup implements StringLookup {

    /**
     * Internal map that stores string suppliers keyed by variable names.
     * Each supplier is responsible for generating the string value when requested.
     * Using ConcurrentHashMap to ensure thread safety for concurrent operations.
     */
    private final Map<String, Supplier<String>> supplierMap = new ConcurrentHashMap<>();

    /**
     * Looks up a string value for the given key by retrieving and invoking the corresponding supplier.
     *
     * <p>If the key exists in the map, the associated supplier is invoked to generate the current value.
     * If the key does not exist, null is returned.</p>
     *
     * @param key the key to look up
     * @return the string value produced by the supplier associated with the key, or null if the key is not found
     */
    @Override
    public String lookup(String key) {
        Supplier<String> supplier = supplierMap.get(key);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Registers a <b>STRING SUPPLIER</b> for the specified key.
     *
     * <p>This method associates a supplier with a key in the internal map. The supplier will be invoked
     * each time the key is looked up, allowing for dynamic value generation.</p>
     *
     * <p>If a supplier is already registered for the given key, it will be replaced by the new supplier.</p>
     *
     * @param key the key to associate with the supplier
     * @param stringSupplier the supplier that will generate the string value when the key is looked up
     */
    public void addStringSupplier(String key, Supplier<String> stringSupplier) {
        supplierMap.put(key, stringSupplier);
    }

}
