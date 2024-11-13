package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.EmailServiceType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class EmailServiceTypeReader implements Converter<String, EmailServiceType> {

    @Override
    public EmailServiceType convert(String source) {
        return EmailServiceType.fromString(source); // Use a helper to handle conversion
    }
}
