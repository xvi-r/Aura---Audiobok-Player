package com.example.audiobooks.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.audiobooks.dto.user.UserRegisterRequest;
import com.example.audiobooks.service.SetupService;
import com.example.audiobooks.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> setup(
            @RequestBody UserRegisterRequest request) {
        
        userService.registerUser(request);

        setupService.completeSetup(request);

        return ResponseEntity.noContent().build();
    }
}