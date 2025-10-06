package com.mor.morpheus.controller;

import com.mor.morpheus.dto.LoginRequest;
import com.mor.morpheus.dto.LoginResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        if ("anka".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            return new LoginResponse("login success", "mock-token-" + request.getUsername());
        } else {
            return new LoginResponse("login failed", null);
        }
    }
}
