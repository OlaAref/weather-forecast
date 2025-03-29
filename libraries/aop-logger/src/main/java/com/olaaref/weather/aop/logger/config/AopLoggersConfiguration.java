package com.olaaref.weather.aop.logger.config;

import com.olaaref.weather.aop.logger.advice.after.LogAfterConfig;
import com.olaaref.weather.aop.logger.advice.around.LogAroundConfig;
import com.olaaref.weather.aop.logger.advice.before.LogBeforeConfig;
import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import com.olaaref.weather.aop.logger.template.interpolation.config.StringSubstitutorConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({AopLoggersProperties.class})
@Import({
        StringSubstitutorConfig.class,
        LogBeforeConfig.class,
        LogAfterConfig.class,
        LogAroundConfig.class
})
public class AopLoggersConfiguration {
}
