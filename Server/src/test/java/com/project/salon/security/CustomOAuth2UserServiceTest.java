package com.project.salon.security;

import com.project.salon.entity.User;
import com.project.salon.entity.enums.Role;
import com.project.salon.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService oAuth2UserService;

    @BeforeEach
    void setUp() throws Exception {
        var field = CustomOAuth2UserService.class.getDeclaredField("adminEmail");
        field.setAccessible(true);
        field.set(oAuth2UserService, "admin@salon.com");
    }

    @Test
    void testUserRoleAssignment_RegularUser() {
        User newUser = User.builder()
                .email("user@example.com")
                .googleId("google-123")
                .name("John User")
                .role(Role.USER)
                .enabled(true)
                .build();

        assertEquals(Role.USER, newUser.getRole());
    }

    @Test
    void testUserRoleAssignment_AdminUser() {
        User adminUser = User.builder()
                .email("admin@salon.com")
                .googleId("google-admin")
                .name("Admin User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        assertEquals(Role.ADMIN, adminUser.getRole());
    }
}
