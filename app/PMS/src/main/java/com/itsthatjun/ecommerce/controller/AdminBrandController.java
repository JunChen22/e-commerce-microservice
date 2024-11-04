package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.service.admin.AdminBrandServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand/admin")
@Tag(name = "Brand related", description = "Brand management service controller")
public class AdminBrandController {

    private final AdminBrandServiceImpl brandService;

    @Autowired
    public AdminBrandController(AdminBrandServiceImpl brandService) {
        this.brandService = brandService;
    }
}
