package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.type.UpdateActionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UpdateActionReader implements Converter<String, UpdateActionType> {

    @Override
    public UpdateActionType convert(String source) {
        return UpdateActionType.fromString(source); // Use a helper to handle conversion
    }
}
