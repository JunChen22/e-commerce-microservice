package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.SourceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class SourceTypeReader implements Converter<String, SourceType> {

    @Override
    public SourceType convert(String source) {
        return SourceType.fromString(source); // Use a helper to handle conversion
    }
}
