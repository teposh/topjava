package ru.javawebinar.topjava.converters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.Set;

@Configuration
public class ConvertersConfiguration {
    // https://stackoverflow.com/questions/46361184/how-to-use-custom-spring-converter-to-convert-xml-config-values
    @Bean
    public ConversionService conversionService(Set<Converter<?, ?>> converters) {
        final ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        converters.add(new StringToLocalDateConverter());
        converters.add(new StringToLocalTimeConverter());
        factory.setConverters(converters);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
