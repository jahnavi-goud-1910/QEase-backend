package com.qease.controller;

import com.qease.dto.AuthDto;
import com.qease.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.JwtResponse> authenticateUser(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        AuthDto.JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.UserDto> registerUser(@Valid @RequestBody AuthDto.RegisterRequest registerRequest) {
        AuthDto.UserDto userDto = authService.register(registerRequest);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDto.UserDto> getCurrentUser(Authentication authentication) {
        AuthDto.UserDto userDto = authService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(userDto);
    }
}
