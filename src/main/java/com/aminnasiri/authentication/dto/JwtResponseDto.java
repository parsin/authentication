package com.aminnasiri.authentication.dto;

import lombok.Data;

@Data
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;

    public JwtResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
