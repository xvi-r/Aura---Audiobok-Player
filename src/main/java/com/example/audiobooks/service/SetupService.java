package com.example.audiobooks.service;

import org.springframework.stereotype.Service;

import com.example.audiobooks.application.ApplicationState;
import com.example.audiobooks.dto.user.UserRegisterRequest;
import com.example.audiobooks.entity.ApplicationStateEntity;
import com.example.audiobooks.repository.ApplicationStateRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Service
public class SetupService{

    private final ApplicationState applicationState;
    private final UserService userService;
    private final ApplicationStateRepository applicationStateRepository;

    public boolean isSetupCompleted() {
        return !applicationState.isSetupRequired();
    }

    @Transactional 
    public void completeSetup(UserRegisterRequest request) {
        userService.upgradeUserToAdmin(request.getUsername());
        ApplicationStateEntity applicationStateEntity = applicationStateRepository.findById(1L).orElseThrow();
        applicationStateEntity.setSetupCompleted(true);
        applicationState.setSetupRequired(false);
    }
}