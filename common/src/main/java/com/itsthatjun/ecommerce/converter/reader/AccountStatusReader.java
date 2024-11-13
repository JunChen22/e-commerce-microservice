package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class AccountStatusReader implements Converter<String, AccountStatus> {

    @Override
    public AccountStatus convert(String source) {
        return AccountStatus.fromString(source); // Use a helper to handle conversion
    }
}

