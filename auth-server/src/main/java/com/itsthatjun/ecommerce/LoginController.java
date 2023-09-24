package com.itsthatjun.ecommerce;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;

@RestController
public class LoginController {

    @GetMapping("/somesome")
    public String somesome(){
        System.out.println("at auth some some  page");
        return "somesome";
    }

    @GetMapping("/login")
    public String login(){
        System.out.println("at auth login page");
        return "at auth login page";
    }
}
