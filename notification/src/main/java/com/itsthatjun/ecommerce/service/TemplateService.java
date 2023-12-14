package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.mapper.EmailTemplatesMapper;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplates;
import com.itsthatjun.ecommerce.mbg.model.EmailTemplatesExample;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class TemplateService {

    private final ResourceLoader resourceLoader;

    private final EmailTemplatesMapper templatesMapper;

    public TemplateService(ResourceLoader resourceLoader, EmailTemplatesMapper templatesMapper) {
        this.resourceLoader = resourceLoader;
        this.templatesMapper = templatesMapper;
    }

    public String getTemplate(String template) {
            EmailTemplatesExample templatesExample = new EmailTemplatesExample();
            templatesExample.createCriteria().andServiceNameEqualTo(template);  // TODO: might change it to use enum
            List<EmailTemplates> templatesList = templatesMapper.selectByExample(templatesExample);

            if (templatesList.isEmpty()) throw new RuntimeException("can not find template for: " + template);

            String templates = templatesList.get(0).getTemplateText();
            return templates;
    }

    public String loadTemplate(String templateName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".txt");
        Path path = resource.getFile().toPath();
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
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

}
