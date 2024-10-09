package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.service.impl.TemplateServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "TestController", description = "Test controller")
public class TestController {

    private final TemplateServiceImpl templateService;

    @Autowired
    public TestController(TemplateServiceImpl templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/test")
    public String test1() {
        return "Hello, World!";
    }

    @PostMapping("/test")
    public Mono<String> test() {
        return templateService.createTemplate("test", "test").then(
                Mono.just("test post!")
        );
    }
}
