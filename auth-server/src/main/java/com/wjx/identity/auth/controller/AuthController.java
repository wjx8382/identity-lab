package com.wjx.identity.auth.controller;

import com.wjx.identity.auth.dto.LoginReponse;
import com.wjx.identity.auth.dto.LoginRequest;
import com.wjx.identity.auth.dto.RegisterRequest;
import com.wjx.identity.auth.service.AuthService;
import com.wjx.identity.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginReponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/test-token")
    public String testToken(@RequestParam String token) {
        return jwtService.extractUsername(token);
    }
}
