package com.thinkitve.aidemo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Health First Provider API")
                        .description("API for Healthcare Provider Registration and Management")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Health First Support")
                                .email("support@healthfirst.com"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://your-docs-url.com"));
    }
} 