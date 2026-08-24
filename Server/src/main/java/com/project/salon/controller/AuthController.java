package com.project.salon.controller;

import com.project.salon.dto.UserResponse;
import com.project.salon.security.CurrentUser;
import com.project.salon.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for authenticated user information and logout")
public class AuthController {

    public AuthController() {
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser UserPrincipal principal) {
        UserResponse response = UserResponse.builder()
                .id(principal.getUser().getId())
                .googleId(principal.getUser().getGoogleId())
                .name(principal.getUser().getName())
                .email(principal.getUser().getEmail())
                .profileImage(principal.getUser().getProfileImage())
                .role(principal.getUser().getRole())
                .enabled(principal.getUser().isEnabled())
                .createdAt(principal.getUser().getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current user and invalidate session")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
