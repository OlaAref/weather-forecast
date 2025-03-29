package com.olaaref.weather.aop.logger.advice.around;

import com.olaaref.weather.aop.logger.properties.AopLoggersProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LogAroundConfig {
    @Bean
    public LogAroundAdvice logAroundAdvice() {
        return new LogAroundAdvice();
    }

    @Bean
    public LogAroundService logAroundService(AopLoggersProperties aopLoggersProperties) {
        return new LogAroundService(aopLoggersProperties);
    }
}
