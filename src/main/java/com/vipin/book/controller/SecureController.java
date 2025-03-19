package com.vipin.book.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vip/v2")
@Tag(name = "Secure request")
public class SecureController {
    @GetMapping("/secure")
    public ResponseEntity<String> secure() {
        return ResponseEntity.ok("jio sher");
    }
}
