package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.ProductCondition;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ProductConditionReader implements Converter<String, ProductCondition> {

    @Override
    public ProductCondition convert(String source) {
        return ProductCondition.fromString(source); // Use a helper to handle conversion
    }
}
