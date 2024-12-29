package com.aminnasiri.authentication.util;

import com.aminnasiri.authentication.entity.Token;
import com.aminnasiri.authentication.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey; // Use Base64-encoded secret if externalized

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private Key signingKey;

    private final TokenRepository tokenRepository;

    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isEmpty()) {
            byte[] secretKeyBytes = Base64.getDecoder().decode(secretKey);
            this.signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
        } else {
            // If no key is provided in properties, generate one
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    public Token generateAccessToken(String username, String role) {
        Date expiration = new Date(System.currentTimeMillis() + accessTokenExpiration);
        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        return new Token(
                token,
                username,
                expiration.toInstant().toEpochMilli());
    }

    public Token generateRefreshToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpiration);
        String token = Jwts.builder()
                .setSubject(username)  // Store the username as the subject of the token
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        return new Token(
                token,
                username,
                expiration.toInstant().toEpochMilli()
        );
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        return getClaimFromToken(token, Claims::getExpiration).before(new Date());
    }

    public Boolean isAccessTokenValid(String accessTokenString) {
        if (!this.isTokenExpired(accessTokenString)) {
            String username = this.getClaimFromToken(accessTokenString, Claims::getSubject);
            // If token is valid, return false
            if (username == null) {
                return false;
            }
            Token accessToken = tokenRepository.findAccessTokenByUsername(username);
            return accessToken != null && accessToken.getExpiryTime() >= System.currentTimeMillis() &&
                    accessToken.getToken().equals(accessTokenString);
        }
        return false;
    }
}