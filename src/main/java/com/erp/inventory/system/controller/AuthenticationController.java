package com.erp.inventory.system.controller;

import com.erp.inventory.system.dto.AuthenticationRequest;
import com.erp.inventory.system.dto.AuthenticationResponse;
import com.erp.inventory.system.dto.RegisterRequest;
import com.erp.inventory.system.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "APIs for User Registration and Login")
public class AuthenticationController {
    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("Register request received: " + request);
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        System.out.println("Authentication request received: " + request);
        return ResponseEntity.ok(service.authenticate(request));
    }
}
