package com.olaaref.weather.aop.logger.advice.before;

import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LogBeforeConfig {
    @Bean
    public LogBeforeAdvice logBeforeAdvice() {
        return new LogBeforeAdvice();
    }

    @Bean
    public LogBeforeService logBeforeService(AopLoggersProperties aopLoggersProperties) {
        return new LogBeforeService(aopLoggersProperties);
    }
}
