package com.itsthatjun.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationService {

    private final TemplateService templateService;

    @Autowired
    public NotificationService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public String generateUserNotification(String name) throws IOException {
        String userTemplate = templateService.loadTemplate("user_template");
        return userTemplate.replace("{name}", name);
    }
}
