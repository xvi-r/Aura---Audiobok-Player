package com.example.audiobooks.service;

import java.beans.Transient;
import java.lang.foreign.Linker.Option;

import org.apache.el.stream.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.audiobooks.dto.user.UserLoginRequest;
import com.example.audiobooks.dto.user.UserRegisterRequest;
import com.example.audiobooks.dto.user.UserRegisterResponse;
import com.example.audiobooks.entity.User;
import com.example.audiobooks.entity.UserRole;
import com.example.audiobooks.exception.UserNameAlreadyExistsException;
import com.example.audiobooks.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration rejected - username '{}' already exists", request.getUsername());
            throw new UserNameAlreadyExistsException("Username Already Exists");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordHash);

        userRepository.save(user);
        log.info("Registered new user account: id={}, username={}", user.getId(), user.getUsername());

        return new UserRegisterResponse(user.getId(), user.getUsername());
    }

    public Authentication login(UserLoginRequest request) {
        log.debug("Authenticating user credentials for username: {}", request.getUsername());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword());

        Authentication auth = authenticationManager.authenticate(token);
        log.info("Successfully authenticated user: username={}", request.getUsername());
        return auth;
    }

    @Transactional 
    public void upgradeUserToAdmin(String username) {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() ->
                new UsernameNotFoundException("User was not found"));
        user.setRole(UserRole.ADMIN);
        log.info("Successfully upgraded user: username={} to admin", user.getUsername());
    }

}
