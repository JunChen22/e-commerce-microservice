package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PublishStatusReader implements Converter<String, PublishStatus> {

    @Override
    public PublishStatus convert(String source) {
        return PublishStatus.fromString(source); // Use a helper to handle conversion
    }
}
