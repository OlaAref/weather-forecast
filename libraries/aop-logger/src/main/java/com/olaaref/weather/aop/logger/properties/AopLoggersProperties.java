package com.olaaref.weather.aop.logger.properties;

import com.olaaref.weather.aop.logger.enums.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = AopLoggersProperties.PREFIX)
public class AopLoggersProperties {

    public static final String PREFIX = "weather.aop.logger";

    /** Whether to enable AOP Loggers */
    private boolean enabled = true;

    /**
     * ENTERING MESSAGE PROPERTIES
     */

    /** Log Level for entering message */
    private Level enteringLevel = Level.DEBUG;

    /** Entering message template */
//    @NotBlank
    private String enteringMessage = "Entering [{method}] with parameters [{parameters}]";

    /**
     * EXITED NORMALLY MESSAGE PROPERTIES
     */

    /** Log Level for exited normally message */
    @NotNull
    private Level exitedLevel = Level.DEBUG;

    /** Exited normally message template */
    @NotBlank
    private String exitedMessage = "[{method}] exited normally with return value [{return-value}]";

    /**
     * EXITED ABNORMALLY MESSAGE PROPERTIES
     */

    /** Log Level for exited abnormally message */
    @NotNull private Level exitedAbnormallyLevel = Level.ERROR;

    /** Exited abnormally message template */
    @NotBlank
    private String exitedAbnormallyMessage = "[{method}] exited abnormally with exception [{exception}]";

    /** Exceptions that will be ignored by Logger */
    private Class<? extends Throwable>[] ignoreExceptions;

    /** Log Level for elapsed message */
    @NotNull private Level elapsedLevel = Level.DEBUG;

    /** Elapsed message template */
    @NotBlank private String elapsedMessage = "[{method}] elapsed [{elapsed}]";

    /** Log Level for elapsed warning message */
    @NotNull private Level elapsedWarningLevel = Level.WARN;

    /** Elapsed warning message template */
    @NotBlank
    private String elapsedWarningMessage = "[{method}] reached elapsed time limit [{elapsed-time-limit}]";

}
