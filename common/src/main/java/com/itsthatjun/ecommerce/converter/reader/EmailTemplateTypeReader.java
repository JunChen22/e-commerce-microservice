package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.EmailTemplateType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class EmailTemplateTypeReader implements Converter<String, EmailTemplateType> {

    @Override
    public EmailTemplateType convert(String source) {
        return EmailTemplateType.fromString(source); // Use a helper to handle conversion
    }
}
