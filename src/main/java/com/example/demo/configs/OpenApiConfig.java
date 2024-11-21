package com.example.demo.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @io.swagger.v3.oas.annotations.info.Info(
        contact = @Contact(name = "ngọc dương", email = "duong2lophot@gmail.com", url = "http://ngocdduong.com"),
        description = "Open API documentation for Spring Boot",
        title = "OPEN API",
        version = "1.0",
        license = @License(name = "License name")
    ),
    servers = {
        @Server(description = "Local env", url = "http://localhost:5000"),
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth",
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER,
    type = SecuritySchemeType.HTTP
)
public class OpenApiConfig {
}


