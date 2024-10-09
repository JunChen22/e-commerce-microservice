package com.itsthatjun.ecommerce.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final TemplateServiceImpl templateService;

    @Autowired
    public NotificationService(TemplateServiceImpl templateService) {
        this.templateService = templateService;
    }

    /**
     * Generate a user notification.
     * @param name the name of the user
     * @return the user notification
     * @throws IOException if the template cannot be loaded
     */
    public String generateUserNotification(String name) throws IOException {
        String userTemplate = templateService.loadTemplate("user_template");
        return userTemplate.replace("{name}", name);
    }
}
