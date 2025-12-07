package com.example.phoebe.controller;

import com.example.phoebe.dto.response.UserDto;
import com.example.phoebe.entity.User;
import com.example.phoebe.mapper.UserMapper;
import com.example.phoebe.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Get current authenticated user information.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
