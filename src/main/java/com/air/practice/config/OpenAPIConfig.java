package com.air.practice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Store API")
                        .version("0.0.1-SNAPSHOT")
                        .description("A comprehensive REST API for managing users and products with JWT authentication")
                        .contact(new Contact()
                                .name("Development Team")
                                .url("https://github.com/GheorgheStefan/store")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Proprietary License")
                                .url("https://example.com/license")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token (obtained from /users/login endpoint)")));
    }
}
