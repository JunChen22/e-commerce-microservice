package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // recommendations
}
