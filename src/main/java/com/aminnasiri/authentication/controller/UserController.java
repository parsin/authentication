package com.aminnasiri.authentication.controller;

import com.aminnasiri.authentication.entity.User;
import com.aminnasiri.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserInfo(Authentication authentication) {

        String username = authentication.getName();
        User user = userService.getUser(username);

        return ResponseEntity.ok(user);
    }
}
