package com.vipin.book.controller;

import com.vipin.book.dtos.AuthenticationRequest;
import com.vipin.book.dtos.AuthenticationResponse;
import com.vipin.book.dtos.RegisterRequest;
import com.vipin.book.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/Authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {

        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<Boolean> confirm(@RequestParam String token) {
        boolean isActivated = false;
        service.activateAccount(token);
        isActivated = true;
        return ResponseEntity.ok(isActivated);
    }
}
