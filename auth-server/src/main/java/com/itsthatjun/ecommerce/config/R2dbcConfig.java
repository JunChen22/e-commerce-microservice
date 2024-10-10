package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.converter.StatusReader;
import com.itsthatjun.ecommerce.converter.StatusWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new StatusWriter());
        converters.add(new StatusReader());
        return new R2dbcCustomConversions(converters);
    }
}
