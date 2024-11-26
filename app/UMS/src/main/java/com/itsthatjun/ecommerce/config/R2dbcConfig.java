package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.converter.reader.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions customConverters() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new PublishStatusReader());
        converters.add(new PlatformTypeReader());
        converters.add(new AccountStatusReader());
        converters.add(new VerificationStatusReader());
        converters.add(new LifeCycleStatusReader());
        converters.add(new UpdateActionReader());
        converters.add(new UserActivityReader());
        return new R2dbcCustomConversions(converters);
    }
}