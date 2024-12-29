package com.aminnasiri.authentication.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Token implements Serializable {

    private String token;
    private String username; // Username associated with the token
    private Long expiryTime; // Timestamp for when the token expires

    // Default constructor
    public Token() {}

    public Token(String token, String username, Long expiryTime) {
        this.token = token;
        this.username = username;
        this.expiryTime = expiryTime;
    }
}
