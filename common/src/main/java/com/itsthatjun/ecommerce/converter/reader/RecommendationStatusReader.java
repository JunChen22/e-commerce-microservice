package com.itsthatjun.ecommerce.converter.reader;

import com.itsthatjun.ecommerce.enums.status.RecommendationStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RecommendationStatusReader implements Converter<String, RecommendationStatus> {

    @Override
    public RecommendationStatus convert(String source) {
        return RecommendationStatus.fromString(source); // Use a helper to handle conversion
    }
}
