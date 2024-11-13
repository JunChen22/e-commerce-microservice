package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.PaymentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PaymentTypeReader implements Converter<String, PaymentType> {

    @Override
    public PaymentType convert(String source) {
        return PaymentType.fromString(source); // Use a helper to handle conversion
    }
}
