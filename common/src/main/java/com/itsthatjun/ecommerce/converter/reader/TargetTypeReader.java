package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.TargetType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class TargetTypeReader implements Converter<String, TargetType> {

    @Override
    public TargetType convert(String source) {
        return TargetType.fromString(source); // Use a helper to handle conversion
    }
}

