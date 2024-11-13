package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.DiscountType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class DiscountTypeReader implements Converter<String, DiscountType> {

    @Override
    public DiscountType convert(String source) {
        return DiscountType.fromString(source); // Use a helper to handle conversion
    }
}
