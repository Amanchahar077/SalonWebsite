package com.project.salon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Salon Appointment Booking System API")
                        .version("1.0.0")
                        .description("Production-ready REST backend for Salon Appointment Booking, Razorpay payment gateway integration, Google OAuth 2.0, dynamic slot generation, and transactional concurrency protection.")
                        .contact(new Contact()
                                .name("Salon Engineering Team")
                                .email("support@salon.com")))
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
                .components(new Components()
                        .addSecuritySchemes("OAuth2", new SecurityScheme()
                                .name("OAuth2")
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Google OAuth 2.0 Authentication")));
    }
}
