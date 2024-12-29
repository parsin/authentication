package com.aminnasiri.authentication.service;

import com.aminnasiri.authentication.dto.JwtResponseDto;
import com.aminnasiri.authentication.dto.LoginRequestDto;
import com.aminnasiri.authentication.entity.Token;
import com.aminnasiri.authentication.exception.UnauthorizedUserException;
import com.aminnasiri.authentication.repository.TokenRepository;
import com.aminnasiri.authentication.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Invalidate old tokens, generate new ones and store them
        Token accessToken = this.invalidateAndGenerateNewAccessToken(loginRequestDto.getUsername());
        Token refreshToken = this.invalidateAndGenerateNewRefreshToken(loginRequestDto.getUsername());

        return new JwtResponseDto(accessToken.getToken(), refreshToken.getToken());
    }

    public JwtResponseDto validateAndIssueNewRefreshToken (String refreshTokenString){
        // Extract username from the refresh token
        String username = jwtUtils.getClaimFromToken(refreshTokenString, Claims::getSubject);
        // Validate the refresh token
        Token storedToken = tokenRepository.findRefreshTokenByUsername(username);
        if (storedToken == null || storedToken.getExpiryTime() < System.currentTimeMillis() ||
                !storedToken.getToken().equals(refreshTokenString)) {
            throw new UnauthorizedUserException("Invalid or expired refresh token");
        }

        // Invalidate old tokens, generate new ones and store them
        Token newAccessToken = this.invalidateAndGenerateNewAccessToken(username);
        Token newRefreshToken = this.invalidateAndGenerateNewRefreshToken(username);

        return new JwtResponseDto(newAccessToken.getToken(), newRefreshToken.getToken());
    }

    private Token invalidateAndGenerateNewAccessToken(String username) {
        // invalidate old access token to prevent using it again.
        tokenRepository.invalidateAccessTokenByUsername(username);
        // Generate new access token
        Token newAccessToken = jwtUtils.generateAccessToken(username, "ROLE_USER");
        // Save new tokens in Redis
        tokenRepository.saveAccessToken(newAccessToken);
        return newAccessToken;
    }

    private Token invalidateAndGenerateNewRefreshToken(String username) {
        // invalidate old refresh token to prevent using it again.
        tokenRepository.invalidateRefreshTokenByUsername(username);
        // Generate new refresh token
        Token newRefreshToken = jwtUtils.generateRefreshToken(username);
        // Save new tokens in Redis
        tokenRepository.saveRefreshToken(newRefreshToken);
        return newRefreshToken;
    }

}
