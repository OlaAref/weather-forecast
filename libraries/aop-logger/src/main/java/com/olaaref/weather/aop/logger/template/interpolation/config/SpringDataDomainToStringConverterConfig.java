package com.olaaref.weather.aop.logger.template.interpolation.config;

import com.olaaref.weather.aop.logger.template.interpolation.converter.PageToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.converter.PageableToStringConverter;
import com.olaaref.weather.aop.logger.template.interpolation.converter.SliceToStringConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * This class is used to conditionally register this configuration class
 * and its beans only when the specified Spring Data classes ( Pageable , Slice , and Page ) are present in the classpath.
 */
@ConditionalOnClass({Slice.class, Page.class, Pageable.class})
@Configuration
public class SpringDataDomainToStringConverterConfig {

    @Bean
    public SliceToStringConverter sliceToStringConverter() {
        return new SliceToStringConverter();
    }

    @Bean
    public PageToStringConverter pageToStringConverter() {
        return new PageToStringConverter();
    }

    @Bean
    public PageableToStringConverter pageableToStringConverter() {
        return new PageableToStringConverter();
    }
}
