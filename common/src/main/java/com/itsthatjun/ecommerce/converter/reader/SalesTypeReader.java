package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.SalesType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class SalesTypeReader implements Converter<String, SalesType> {

    @Override
    public SalesType convert(String source) {
        return SalesType.fromString(source); // Use a helper to handle conversion
    }
}
