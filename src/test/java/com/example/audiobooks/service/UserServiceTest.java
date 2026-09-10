package com.example.audiobooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.audiobooks.entity.User;
import com.example.audiobooks.entity.UserRole;
import com.example.audiobooks.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("upgradeUserToAdmin upgrades existing user role from USER to ADMIN")
    void upgradeUserToAdmin_WhenUserExists_UpgradesRoleToAdmin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole(UserRole.USER);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        userService.upgradeUserToAdmin("admin");

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("upgradeUserToAdmin throws UsernameNotFoundException when user does not exist")
    void upgradeUserToAdmin_WhenUserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.upgradeUserToAdmin("unknown"));
    }
}
