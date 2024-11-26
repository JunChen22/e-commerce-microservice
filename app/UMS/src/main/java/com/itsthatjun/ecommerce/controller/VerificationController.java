package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.service.impl.VerificationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/verification")
@Tag(name = "verification related", description = "verify user information")
public class VerificationController {

    private final VerificationServiceImpl verificationService;

    @Autowired
    public VerificationController(VerificationServiceImpl verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/mobile")
    private Mono<ResponseEntity<String>> mobileVerification(@RequestParam String authCode) {
        return verificationService.verifyAuthCode(authCode)
                .map(verified -> verified ? ResponseEntity.ok("Mobile number verified") : ResponseEntity.badRequest().body("Invalid auth code"));
    }

    @GetMapping("/email")
    private Mono<ResponseEntity<String>> emailVerification(@RequestParam String verificationToken) {
        return verificationService.verifyVerificationToken(verificationToken)
                .map(verified -> verified ? ResponseEntity.ok("Email verified") : ResponseEntity.badRequest().body("Invalid verification token"));
    }
}
