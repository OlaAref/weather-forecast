package com.olaaref.weather.aop.logger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that certain parameters or fields should be excluded from logging.
 * <p>
 * This annotation can be applied to:
 * <ul>
 *   <li>Method parameters in web controller methods to prevent logging of sensitive data</li>
 *   <li>Classes used within service contexts to mark them as containing sensitive data</li>
 *   <li>Annotation classes for meta-annotation purposes</li>
 *   <li>Methods to indicate their parameters shouldn't be logged</li>
 * </ul>
 *
 * <p>When applied without specifying parameters, all fields of the annotated element will be excluded from logging.
 * When specific parameters are provided, only those named fields will be excluded while other fields will remain logged.
 *
 * <p><b>Example usage:</b>
 * <pre>{@code
 * @PostMapping
 * public ResponseEntity<?> createUser(
 *     @RequestBody @DoNotLog({"password", "ssn"}) User user) {
 *     // user's password and ssn won't be logged, other fields will be
 * }
 *
 * @DoNotLog // All fields of this class won't be logged
 * public class SensitiveData {
 *     // ...
 * }
 *
 * public ResponseEntity<?> createUser(
 *     @DoNotLog String password, @DoNotLog String ssn, String name) {
 *     // password and ssn won't be logged, name will be
 * }
 *
 * }</pre>
 */
@Target({
        ElementType.PARAMETER,
        ElementType.FIELD,
        ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoNotLog {

    /**
     * Specifies which fields should be excluded from logging.
     * <p>
     * When empty (default), all fields of the annotated element will be excluded from logging.
     * When specified, only the named fields will be excluded while other fields will be logged normally.
     *
     * @return array of field names to exclude from logging
     */
    String[] parameters() default {};
}
