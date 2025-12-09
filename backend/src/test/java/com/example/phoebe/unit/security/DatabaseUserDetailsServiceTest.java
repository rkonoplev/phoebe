package com.example.phoebe.unit.security;

import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.security.DatabaseUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DatabaseUserDetailsService userDetailsService;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role("ADMIN", "Administrator role");
        adminRole.setId(1L);

        Role editorRole = new Role("EDITOR", "Editor role");
        editorRole.setId(2L);

        activeUser = new User("admin", "$2a$10$encodedPassword", "admin@test.com", true);
        activeUser.setId(1L);
        activeUser.setRoles(Set.of(adminRole, editorRole));

        inactiveUser = new User("inactive", "$2a$10$encodedPassword", "inactive@test.com", false);
        inactiveUser.setId(2L);
        inactiveUser.setRoles(Set.of(editorRole));
    }

    @Test
    void loadUserByUsernameExistingActiveUserReturnsUserDetails() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EDITOR")));
    }

    @Test
    void loadUserByUsernameInactiveUserReturnsDisabledUserDetails() {
        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(inactiveUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("inactive");

        assertNotNull(userDetails);
        assertEquals("inactive", userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameNonExistingUserThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertTrue(exception.getMessage().contains("User not found: nonexistent"));
    }

    @Test
    void loadUserByUsernameCallsRepository() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeUser));

        userDetailsService.loadUserByUsername("admin");

        verify(userRepository, times(1)).findByUsername("admin");
    }
}
