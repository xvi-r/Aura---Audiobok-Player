package com.example.audiobooks.application;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.example.audiobooks.entity.ApplicationStateEntity;
import com.example.audiobooks.repository.ApplicationStateRepository;
import com.example.audiobooks.service.SetupService;

@RequiredArgsConstructor 
@Component
public class ApplicationStartup {

    private final ApplicationStateRepository stateRepository;
    private final ApplicationState applicationState;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

       ApplicationStateEntity state =
                stateRepository.findById(1L)
                        .orElseGet(() -> {
                ApplicationStateEntity newState = new ApplicationStateEntity();
                newState.setId(1L);
                newState.setSetupCompleted(false);
                return stateRepository.save(newState);
            });
        
         applicationState.setSetupRequired(
                !state.isSetupCompleted());
    }
}