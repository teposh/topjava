package ru.javawebinar.topjava.converters;

import org.springframework.core.convert.converter.Converter;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalTime;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(final String source) {
        return DateTimeUtil.parseLocalTime(source);
    }
}
