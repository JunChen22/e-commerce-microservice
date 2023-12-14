package com.itsthatjun.ecommerce.config.batch;

import com.itsthatjun.ecommerce.mbg.model.Email;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class reader implements ItemReader<Email> {


    private Iterator<Email> ordersIterator;

    @Override
    public Email read() throws Exception {
        return new Email();
    }
}
