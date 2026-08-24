package com.project.salon.security;

import com.project.salon.entity.User;
import com.project.salon.entity.enums.Role;
import com.project.salon.exception.UnauthorizedException;
import com.project.salon.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    @Value("${app.admin-email:admin@salon.com}")
    private String adminEmail;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user: ", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.isEnabled()) {
                throw new UnauthorizedException("User account is disabled");
            }
            user.setGoogleId(googleId);
            if (picture != null) {
                user.setProfileImage(picture);
            }
            if (name != null) {
                user.setName(name);
            }
            user = userRepository.save(user);
            log.info("Existing user logged in via OAuth2: {}", email);
        } else {
            Role assignedRole = email.equalsIgnoreCase(adminEmail) ? Role.ADMIN : Role.USER;
            user = User.builder()
                    .googleId(googleId)
                    .email(email)
                    .name(name != null ? name : "Google User")
                    .profileImage(picture)
                    .role(assignedRole)
                    .enabled(true)
                    .build();
            user = userRepository.save(user);
            log.info("New user registered via OAuth2: {} with role {}", email, assignedRole);
        }

        return new UserPrincipal(user, attributes);
    }
}
