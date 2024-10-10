package com.itsthatjun.ecommerce.converter;

import com.itsthatjun.ecommerce.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class StatusWriter implements Converter<Status, Integer> {
    @Override
    public Integer convert(Status source) {
        return source != null ? source.getValue() : null;
    }
}
