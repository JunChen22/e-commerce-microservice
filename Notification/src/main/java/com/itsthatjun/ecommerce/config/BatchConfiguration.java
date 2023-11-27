package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.mbg.model.Email;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                   ItemReader<Email> orderItemReader,
                   ItemProcessor<Email, Email> orderItemProcessor,
                   ItemWriter<Email> orderItemWriter) {

        Step step = stepBuilderFactory
                .get("emailStep")
                .<Email, Email>chunk(2)
                .reader(orderItemReader)
                .processor(orderItemProcessor)
                .writer(orderItemWriter)
                .build();

        return jobBuilderFactory
                .get("job")
                .start(step)
                .build();
    }
}
