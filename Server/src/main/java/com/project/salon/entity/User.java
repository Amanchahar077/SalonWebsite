package com.project.salon.entity;

import com.project.salon.entity.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_google_id", columnList = "googleId")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image", length = 2048)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(Long id, String googleId, String name, String email, String profileImage, Role role, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.googleId = googleId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class UserBuilder {
        private Long id;
        private String googleId;
        private String name;
        private String email;
        private String profileImage;
        private Role role;
        private boolean enabled = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder googleId(String googleId) { this.googleId = googleId; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            return new User(id, googleId, name, email, profileImage, role, enabled, createdAt, updatedAt);
        }
    }
}
