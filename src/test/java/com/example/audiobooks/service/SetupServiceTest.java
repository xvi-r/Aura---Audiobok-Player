package com.example.audiobooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.audiobooks.application.ApplicationState;
import com.example.audiobooks.dto.user.UserRegisterRequest;
import com.example.audiobooks.entity.ApplicationStateEntity;
import com.example.audiobooks.repository.ApplicationStateRepository;

@ExtendWith(MockitoExtension.class)
public class SetupServiceTest {

    @Mock
    private ApplicationState applicationState;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationStateRepository applicationStateRepository;

    @InjectMocks
    private SetupService setupService;

    @Test
    @DisplayName("isSetupCompleted returns true when setup is not required in memory")
    void isSetupCompleted_WhenSetupNotRequired_ReturnsTrue() {
        when(applicationState.isSetupRequired()).thenReturn(false);

        boolean completed = setupService.isSetupCompleted();

        assertThat(completed).isTrue();
    }

    @Test
    @DisplayName("completeSetup upgrades user to admin and sets setupCompleted in memory & DB")
    void completeSetup_UpgradesUserAndSetsSetupCompleted() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("admin");
        request.setPassword("password123");
        ApplicationStateEntity entity = new ApplicationStateEntity();
        entity.setId(1L);
        entity.setSetupCompleted(false);

        when(applicationStateRepository.findById(1L)).thenReturn(Optional.of(entity));

        setupService.completeSetup(request);

        verify(userService).upgradeUserToAdmin("admin");
        assertThat(entity.isSetupCompleted()).isTrue();
        verify(applicationState).setSetupRequired(false);
    }
}
