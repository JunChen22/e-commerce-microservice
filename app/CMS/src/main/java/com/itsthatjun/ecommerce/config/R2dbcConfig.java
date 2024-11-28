package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.converter.reader.LifeCycleStatusReader;
import com.itsthatjun.ecommerce.converter.reader.PublishStatusReader;
import com.itsthatjun.ecommerce.converter.reader.UpdateActionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions customConverters() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new PublishStatusReader());
        converters.add(new LifeCycleStatusReader());
        converters.add(new UpdateActionReader());
        return new R2dbcCustomConversions(converters);
    }
}