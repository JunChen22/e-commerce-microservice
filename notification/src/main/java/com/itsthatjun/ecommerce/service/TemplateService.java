package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.model.EmailTemplates;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface TemplateService {

    /**
     * get template from database
     *
     * @param serviceName
     * @return EmailTemplates
     */
    Mono<EmailTemplates> getTemplate(String serviceName);

    /**
     * Loads a template from a file, resource/templateName.txt
     *
     * @param templateName the name of the template to load
     * @return the content of the template as a String
     * @throws IOException if an I/O error occurs while reading the template file
     */
    String loadTemplate(String templateName) throws IOException;


    /**
     * Retrieves all templates from the database.
     *
     * @return a Flux containing all EmailTemplates
     */
    Flux<EmailTemplates> getAllTemplates();

    /**
     * Creates a new template in the database.
     *
     * @param serviceName  the name of the service for the new template
     * @param templateText the content of the new template
     * @return a Mono containing the created EmailTemplates
     */
    Mono<EmailTemplates> createTemplate(String serviceName, String templateText);

    /**
     * Updates an existing template in the database.
     *
     * @param serviceName  the name of the service for the template to update
     * @param templateText the new content of the template
     * @return a Mono containing the updated EmailTemplates
     */
    Mono<EmailTemplates> updateTemplate(String serviceName, String templateText);

    /**
     * Deletes a template from the database.
     *
     * @param serviceName  the name of the service for the template to delete
     * @param templateText the content of the template to delete
     * @return a Mono signaling when the delete operation is complete
     */
    Mono<Void> deleteTemplate(String serviceName, String templateText);
}
