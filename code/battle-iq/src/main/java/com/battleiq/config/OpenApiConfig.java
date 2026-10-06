package com.battleiq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI battleIqOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Battle IQ API")
                        .description("REST API ของเกมตอบคำถาม Battle IQ")
                        .version("v1"));
    }
}