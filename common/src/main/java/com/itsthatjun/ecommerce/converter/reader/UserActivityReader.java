package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.UserActivityType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UserActivityReader implements Converter<String, UserActivityType> {

    @Override
    public UserActivityType convert(String source) {
        return UserActivityType.fromString(source); // Use a helper to handle conversion
    }
}