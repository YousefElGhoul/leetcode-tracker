package com.ghoul.leetcodetracker.controller;

import com.ghoul.leetcodetracker.model.dto.AuthResponse;
import com.ghoul.leetcodetracker.model.dto.LoginRequest;
import com.ghoul.leetcodetracker.model.dto.RegisterRequest;
import com.ghoul.leetcodetracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        UserDetails userDetails = authService.authenticate(
                request.username(),
                request.password()
        );

        String tokenValue = authService.generateToken(userDetails);

        AuthResponse response = new AuthResponse(
                tokenValue,
                86400,
                userDetails.getUsername()
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request.username(), request.password());
        return ResponseEntity.ok().build();
    }
}
