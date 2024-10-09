package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.model.EmailTemplates;
import com.itsthatjun.ecommerce.repository.EmailTemplatesRepository;
import com.itsthatjun.ecommerce.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TemplateServiceImpl implements TemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final ResourceLoader resourceLoader;

    private final EmailTemplatesRepository emailTemplatesRepository;

    @Autowired
    public TemplateServiceImpl(ResourceLoader resourceLoader, EmailTemplatesRepository emailTemplatesRepository) {
        this.resourceLoader = resourceLoader;
        this.emailTemplatesRepository = emailTemplatesRepository;
    }

    @Override
    public Mono<EmailTemplates> getTemplate(String serviceName) {
        return emailTemplatesRepository.findByServiceName(serviceName)
                .switchIfEmpty(Mono.just(new EmailTemplates()));
    }

    @Override
    public String loadTemplate(String templateName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".txt");
        Path path = resource.getFile().toPath();
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Override
    public Flux<EmailTemplates> getAllTemplates() {
        return null;
    }

    @Override
    public Mono<EmailTemplates> createTemplate(String serviceName, String templateText) {
        EmailTemplates emailTemplates = new EmailTemplates();
        emailTemplates.setServiceName(serviceName);
        emailTemplates.setTemplateText(templateText);

        return emailTemplatesRepository.save(emailTemplates)
                .doOnSuccess(savedTemplate -> LOG.info("Successfully saved EmailTemplate: {}", savedTemplate))
                .switchIfEmpty(Mono.error(new RuntimeException("Failed to create EmailTemplate.")))
                .onErrorResume(e -> {
                    LOG.error("Error creating email template: {}", e.getMessage());
                    return Mono.empty(); // or log the error and return Mono.just(new EmailTemplates());
                });
    }

    @Override
    public Mono<EmailTemplates> updateTemplate(String serviceName, String templateText) {
        return null;
    }

    @Override
    public Mono<Void> deleteTemplate(String serviceName, String templateText) {
        return null;
    }
}
