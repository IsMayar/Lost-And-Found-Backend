package com.findly.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI findlyOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Findly API")
                        .description("AI Lost and Found backend API")
                        .version("v1")
                        .contact(new Contact()
                                .name("Findly Team")
                                .email("support@findly.local"))
                        .license(new License()
                                .name("Private")
                                .url("https://findly.local")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Local development server")
                ));
    }
}
