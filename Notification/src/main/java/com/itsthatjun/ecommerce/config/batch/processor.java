package com.itsthatjun.ecommerce.config.batch;

import com.itsthatjun.ecommerce.mbg.model.Email;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class processor implements ItemProcessor<Email, Email> {

    @Override
    public Email process(Email email) throws Exception {

        System.out.println("processed");
        return email;
    }
}
