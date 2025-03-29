package com.olaaref.weather.aop.logger.advice.after;

import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LogAfterConfig {
    @Bean
    public LogAfterAdvice logAfterAdvice() {
        return new LogAfterAdvice();
    }

    @Bean
    public LogAfterService logAfterService(AopLoggersProperties aopLoggersProperties) {
        return new LogAfterService(aopLoggersProperties);
    }
}
