package com.itsthatjun.ecommerce.converter;

import com.itsthatjun.ecommerce.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StatusReader implements Converter<Integer, Status> {
    @Override
    public Status convert(Integer source) {
        return source != null ? Status.fromValue(source) : null;
    }
}
