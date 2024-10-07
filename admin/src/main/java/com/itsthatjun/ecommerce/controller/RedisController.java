package com.itsthatjun.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@Tag(name = "Redis related", description = "access and modify the current Redis cache for ECom-app")
public class RedisController {
}
