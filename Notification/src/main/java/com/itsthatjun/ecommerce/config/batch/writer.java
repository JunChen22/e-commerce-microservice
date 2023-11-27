package com.itsthatjun.ecommerce.config.batch;

import com.itsthatjun.ecommerce.mbg.model.Email;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class writer implements ItemWriter <Email> {

    @Override
    public void write(List<? extends Email> list) throws Exception {
        System.out.println("writer " + list.size());
    }
}
