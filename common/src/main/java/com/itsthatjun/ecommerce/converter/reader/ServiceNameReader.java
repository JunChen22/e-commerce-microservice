package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.ServiceName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ServiceNameReader implements Converter<String, ServiceName> {

    @Override
    public ServiceName convert(String source) {
        return ServiceName.fromString(source); // Use a helper to handle conversion
    }
}
