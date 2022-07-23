package ru.javawebinar.topjava.converters;

import org.springframework.core.convert.converter.Converter;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(final String source) {
        return DateTimeUtil.parseLocalDate(source);
    }
}
