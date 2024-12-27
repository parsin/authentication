package com.aminnasiri.authentication.config;

import com.aminnasiri.authentication.filter.JwtAuthenticationFilter;
import com.aminnasiri.authentication.util.JwtUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final JwtUtils jwtUtils;

    public JwtConfigurer(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilter(new JwtAuthenticationFilter(jwtUtils));
    }
}
