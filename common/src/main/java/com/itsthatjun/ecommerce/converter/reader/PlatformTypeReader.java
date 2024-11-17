package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.PlatformType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PlatformTypeReader implements Converter<String, PlatformType> {

    @Override
    public PlatformType convert(String source) {
        return PlatformType.fromString(source); // Use a helper to handle conversion
    }
}
