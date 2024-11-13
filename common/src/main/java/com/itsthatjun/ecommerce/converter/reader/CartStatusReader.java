package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.CartStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class CartStatusReader implements Converter<String, CartStatus> {

    @Override
    public CartStatus convert(String source) {
        return CartStatus.fromString(source); // Use a helper to handle conversion
    }
}
