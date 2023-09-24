package com.itsthatjun.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Login {

    @GetMapping("/success")
    public String sucessController() {
        
        return "at success controller";
    }

    @GetMapping("/auth-server-login")
    public String redirectToAuthServerLoginPage() {
        // Redirect to your custom authentication server's login page
        return "redirect:http://auth-server/login"; // Replace with your auth server's URL
    }
}
