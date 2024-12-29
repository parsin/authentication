package com.aminnasiri.authentication.service;

import com.aminnasiri.authentication.dto.UserDto;
import com.aminnasiri.authentication.entity.User;
import com.aminnasiri.authentication.exception.UserAlreadyExistsException;
import com.aminnasiri.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserDto userDto) {
        if (userRepository.existsByUsername((userDto.getUsername()))) {
            throw new UserAlreadyExistsException("Username '" + userDto.getUsername() + "' is already taken. Please choose another username.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setUsername(userDto.getUsername());
        userRepository.save(user);
    }

    public User getUser(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

}
