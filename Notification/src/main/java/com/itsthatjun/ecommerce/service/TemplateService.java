package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.mapper.EmailTemplatesMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TemplateService {

    private final ResourceLoader resourceLoader;

    private final EmailTemplatesMapper templatesMapper;

    public TemplateService(ResourceLoader resourceLoader, EmailTemplatesMapper templatesMapper) {
        this.resourceLoader = resourceLoader;
        this.templatesMapper = templatesMapper;
    }

    /*
    public String getTemplate(String template) {
            EmailTemplatesExample templatesExample = new EmailTemplatesExample();
            templatesExample.createCriteria().and
    }

    public List<EmailTemplates> getAllTemplates() {
        return templatesMapper.selectByExample(new EmailTemplatesExample());
    }

    public void createTemplate(String serviceName, String templateText) {

    }

    public void updateTemplate(String serviceName, String templateText) {

    }

    public void deleteTemplate(String serviceName, String templateText) {

    }

     */

    public String loadTemplate(String templateName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".txt");
        Path path = resource.getFile().toPath();
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
