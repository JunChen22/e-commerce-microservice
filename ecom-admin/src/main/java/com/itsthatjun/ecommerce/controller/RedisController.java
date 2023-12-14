package com.itsthatjun.ecommerce.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@Api(tags = "Redis related", description = "access and modify the current Redis cache for ECom-app")
public class RedisController {
}
