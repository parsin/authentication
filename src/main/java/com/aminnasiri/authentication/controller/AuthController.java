package com.aminnasiri.authentication.controller;

import com.aminnasiri.authentication.dto.JwtResponseDto;
import com.aminnasiri.authentication.dto.LoginRequestDto;
import com.aminnasiri.authentication.entity.User;
import com.aminnasiri.authentication.repository.UserRepository;
import com.aminnasiri.authentication.util.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Register user logic (validate, save to DB, etc.)
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateAccessToken(loginRequest.getUsername(), "ROLE_USER");
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        return ResponseEntity.ok(new JwtResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshTokenRequest) {
        // Validate refresh token and issue new access token + refresh token
        return ResponseEntity.ok(new JwtResponseDto(newAccessToken, newRefreshToken));
    }
}
