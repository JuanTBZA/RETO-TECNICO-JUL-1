package com.juan.tirado.reto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Reto API",
                version = "1.0",
                description = "Documentacion de endpoints para la API de productos"
        )
)
public class OpenApiConfig {
}
