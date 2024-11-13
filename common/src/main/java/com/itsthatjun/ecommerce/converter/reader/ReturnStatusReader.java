package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.ReturnStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ReturnStatusReader implements Converter<String, ReturnStatus> {

    @Override
    public ReturnStatus convert(String source) {
        return ReturnStatus.fromString(source); // Use a helper to handle conversion
    }
}
