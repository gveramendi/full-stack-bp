package net.veramendi.fullstackbpapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Full Stack BP API",
                version = "1.0.0",
                description = "REST API for managing clients, accounts, movements and account-statement reports.",
                contact = @Contact(name = "BP Full Stack Exam", email = "gerson@veramendi.net")
        ),
        servers = {
                @Server(url = "http://localhost:8080/api", description = "Local")
        }
)
public class OpenApiConfig {
}
