package com.mor.morpheus.controller;

import com.mor.morpheus.dto.LoginRequest;
import com.mor.morpheus.dto.LoginResponse;
import com.mor.morpheus.entity.User;
import com.mor.morpheus.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return new LoginResponse("user not found", null);
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponse("wrong password", null);
        }

        return new LoginResponse("login success", "mock-token-" + request.getUsername());
    }

    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody LoginRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new LoginResponse("username already exists", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        userRepository.save(user);

        return new LoginResponse("register success", "mock-token-" + request.getUsername());
    }
}
