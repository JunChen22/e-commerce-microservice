package com.itsthatjun.ecommerce.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public Mono<Void> sendEmail(String to, String subject, String text) {
        LOG.info("Preparing to send email to: {}, subject: {}", to, subject);

        System.out.println("send email to : "  + to);
        System.out.println("subject : " + subject);
        System.out.println("body : "  + text);

        // Creating a Mono to represent the email sending process
        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(to);
                    message.setSubject(subject);
                    message.setText(text);

                    try {
                        // Blocking operation: Sending email
                        javaMailSender.send(message);
                        LOG.info("Email successfully sent to: {}", to);
                    } catch (Exception e) {
                        LOG.error("Failed to send email to: {}", to, e);
                        throw new RuntimeException("Email sending failed", e);  // Propagate error
                    }
                }).subscribeOn(Schedulers.boundedElastic())  // Run on a bounded elastic scheduler to handle blocking I/O
                .then();  // Signal completion
    }
}
