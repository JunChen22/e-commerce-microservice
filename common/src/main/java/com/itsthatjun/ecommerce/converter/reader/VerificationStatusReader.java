package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class VerificationStatusReader implements Converter<String, VerificationStatus> {

    @Override
    public VerificationStatus convert(String source) {
        return VerificationStatus.fromString(source); // Use a helper to handle conversion
    }
}

