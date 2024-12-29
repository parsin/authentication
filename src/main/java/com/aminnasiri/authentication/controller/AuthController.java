package com.aminnasiri.authentication.controller;

import com.aminnasiri.authentication.dto.LoginRequestDto;
import com.aminnasiri.authentication.dto.RefreshTokenDto;
import com.aminnasiri.authentication.dto.UserDto;
import com.aminnasiri.authentication.exception.UnauthorizedUserException;
import com.aminnasiri.authentication.service.AuthService;
import com.aminnasiri.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshToken)  {
        if(refreshToken.getRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is empty");
        }
        try {
            return ResponseEntity.ok(authService.validateAndIssueNewRefreshToken(refreshToken.getRefreshToken()));
        }catch (UnauthorizedUserException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token. Please log in again.");
        }
    }
}
