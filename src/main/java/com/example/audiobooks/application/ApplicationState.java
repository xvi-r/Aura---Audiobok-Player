package com.example.audiobooks.application;

import org.springframework.stereotype.Component;

@Component
public class ApplicationState {

    private volatile boolean setupRequired;

    public boolean isSetupRequired() {
        return setupRequired;
    }

    public void setSetupRequired(boolean setupRequired) {
        this.setupRequired = setupRequired;
    }
}