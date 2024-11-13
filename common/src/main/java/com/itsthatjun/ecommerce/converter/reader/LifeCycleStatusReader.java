package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LifeCycleStatusReader implements Converter<String, LifeCycleStatus> {

    @Override
    public LifeCycleStatus convert(String source) {
        return LifeCycleStatus.fromString(source); // Use a helper to handle conversion
    }
}
