package com.aminnasiri.authentication.config;

import com.aminnasiri.authentication.filter.JwtAuthenticationFilter;
import com.aminnasiri.authentication.service.AuthService;
import com.aminnasiri.authentication.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final JwtUtils jwtUtils;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilter(new JwtAuthenticationFilter(jwtUtils));
    }
}
